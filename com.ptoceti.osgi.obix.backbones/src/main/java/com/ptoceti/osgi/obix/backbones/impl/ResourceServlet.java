package com.ptoceti.osgi.obix.backbones.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Resource servlet shamelessly copied from felix hhtp service ( I could have simply extended but this would have brought
 * dependencies.)
 * Main goalhere is to serve gz static resources. Each javascript file (.js) has been gzipped gy grunt-contribute-compress during
 * the build phase. This servlet check if the client accept gzip encoding in response, check if the .gz resource exists and in this case
 * serves it.
 * 
 * @author lor
 *
 */
public final class ResourceServlet extends HttpServlet
{
    private final String path;
    private final boolean gzip;

    public ResourceServlet(String path, boolean gzip)
    {
        this.path = path;
        this.gzip = gzip;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        String target = req.getPathInfo();
        if (target == null)
        {
            target = "";
        }

        if (!target.startsWith("/"))
        {
            target += "/" + target;
        }

        String resName = this.path + target;
        URL url = getServletContext().getResource(resName);

        if (url == null)
        {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        else
        {
            handle(req, res, url, resName);
        }
    }

    private void handle(HttpServletRequest req, HttpServletResponse res, URL url, String resName) throws IOException
    {
    	boolean doGzip = false;
    	URL gzipUrl = null;
    	
    	String acceptHeader = req.getHeader("Accept-Encoding");
    	if( this.gzip && acceptHeader != null && acceptHeader.lastIndexOf("gzip") > -1){
    		// client accept gzip ressources
    		gzipUrl = getServletContext().getResource(resName + ".gz");
    		if( gzipUrl != null ){
	    		URLConnection zipConnection = gzipUrl.openConnection();
	    		if (zipConnection != null) {
					doGzip = true;
				}
    		}
    		
    		res.addHeader("Vary", "Accept-Encoding");
    	}
    	
        String contentType = getServletContext().getMimeType(resName);
        if (contentType != null)
        {
            res.setContentType(contentType);
        }

        long lastModified = getLastModified(doGzip ? gzipUrl : url);
        if (lastModified != 0)
        {
            res.setDateHeader("Last-Modified", lastModified);
        }

        if (!resourceModified(lastModified, req.getDateHeader("If-Modified-Since")))
        {
            res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
        else
        {
        	if( doGzip){
        		 res.setHeader("Content-Encoding", "gzip");
        	}
            copyResource(doGzip ? gzipUrl : url, res);
        }
    }

    private long getLastModified(URL url)
    {
        long lastModified = 0;

        try
        {
            URLConnection conn = url.openConnection();
            lastModified = conn.getLastModified();
        }
        catch (Exception e)
        {
            // Do nothing
        }

        if (lastModified == 0)
        {
            String filepath = url.getPath();
            if (filepath != null)
            {
                File f = new File(filepath);
                if (f.exists())
                {
                    lastModified = f.lastModified();
                }
            }
        }

        return lastModified;
    }

    private boolean resourceModified(long resTimestamp, long modSince)
    {
        modSince /= 1000;
        resTimestamp /= 1000;

        return resTimestamp == 0 || modSince == -1 || resTimestamp > modSince;
    }

    private void copyResource(URL url, HttpServletResponse res) throws IOException
    {
        URLConnection conn = null;
        OutputStream os = null;
        InputStream is = null;

        try
        {
            conn = url.openConnection();

            is = conn.getInputStream();
            os = res.getOutputStream();
            // FELIX-3987 content length should be set *before* any streaming is done 
            // as headers should be written before the content is actually written...
            int len = getContentLength(conn);
            if (len >= 0)
            {
                res.setContentLength(len);
            }

            byte[] buf = new byte[1024];
            int n;

            while ((n = is.read(buf, 0, buf.length)) >= 0)
            {
                os.write(buf, 0, n);
            }
        }
        finally
        {
            if (is != null)
            {
                is.close();
            }

            if (os != null)
            {
                os.close();
            }
        }
    }

    private int getContentLength(URLConnection conn)
    {
        int length = -1;

        length = conn.getContentLength();
        if (length < 0)
        {
            // Unknown, try whether it is a file, and if so, use the file 
            // API to get the length of the content...
            String path = conn.getURL().getPath();
            if (path != null)
            {
                File f = new File(path);
                // In case more than 2GB is streamed 
                if (f.length() < Integer.MAX_VALUE)
                {
                    length = (int) f.length();
                }
            }
        }
        return length;
    }
}
