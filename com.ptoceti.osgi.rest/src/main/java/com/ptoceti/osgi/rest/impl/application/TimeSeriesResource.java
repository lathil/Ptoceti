package com.ptoceti.osgi.rest.impl.application;

import com.ptoceti.osgi.rest.impl.TimeSeriesServiceListener;
import com.ptoceti.osgi.rest.impl.application.model.TimeSeriesinfo;
import com.ptoceti.osgi.timeseries.TimeSeriesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("series")
@Tags({@Tag(name = "series")})
public class TimeSeriesResource {

    @Inject
    TimeSeriesServiceListener timeSeriesServiceListener;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public TimeSeriesinfo getTimeSeries() {


        TimeSeriesService timeSeriesService = timeSeriesServiceListener.get();
        TimeSeriesinfo result = new TimeSeriesinfo(timeSeriesService != null ? timeSeriesService.ping() : false);

        return result;
    }
}
