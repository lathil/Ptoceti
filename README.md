Ptoceti : an embedable Obix server
==================================================

Ptoceti is a set of Osgi bundles to build a Obix (' [Open Building Infomation Xchange]( http://www.obix.org/)') server.

Ptoceti is build on top of [Osgi](www.osgi.org) R4 and and execute in a jdk 1.7. It needs about 20-30 mb of memory to execute which make it a nice fit for a small form factor Arm computer.

The architecture of the server rest on a consumer / producer pattern so that you can easely provide your own to data to be exposed through your own bundles.
Additionally an embedded [SQLite](http://sqlite.org) database is used to record data and serves it through a SPA Javascript style client for interaction in mobile and tablet devices.

 
## Ptoceti's modules

Ptoceti's main modules are:

- com.ptoceti.osgi.obix.backbones : the single page web application to serve obix content.

- com.ptoceti.osgi.obix : the obix server itself, splitted into two modules, api and impl

- com.ptoceti.osgi.sqlite : the database used by the server

- com.ptoceti.osgi.wireadmin : the messaging system responsible for transiting messages between producer and consumer ( the server and other data provider )

- com.ptoceti.osgi.modbus : a set of two bundles ( modbus and modbusdevice ) capable to connect to a modbus link ( Rs-485 ) and present data to the wire handler.

- com.ptoceti.osgi.pi : a bundle capable to produce data from inside a raspberry Pi; Make use of the [Pi4j project](https://github.com/Pi4J/pi4j/).


## Compatibility and requirements

Currently, Ptoceti need the following modules:
- rxtxcomm-API 2.1.7
- kxml-LIB 2.3.0
- pax-web-extender-whiteboard 1.0.12
- restlet 2.0.5
- guice 2.0
- sqlite-jdbc 3.7.15
- jackson 2.2

jre or jdk : 1.7


To run, it will need an implementation of Osgi R4. I made it execute on a patch of [Felix](/felix.apache.org) and [Knopflerfish](http://www.knopflerfish.org/) bundles.
It will also need an implementation of Osgi Http server such as [Pax Web](https://github.com/ops4j/org.ops4j.pax.web)
 
##Getting started

### Build from sources

You'll need maven 3.x installed for compiling and packaging the bundles. For building the Spa project ( com.ptoceti.osgi.obix.backbones ), you'll need node.js, npm and Grunt installed as well.
Assure that everything is available from the current path and under the shell simply enter

'''
maven clean install
'''

If you want to execute the freshly build bundles for debugging or testing, you can launch a full settup using the include maven-pax-plugin by isuing the command
'''
maven pax:run
'''
For remote debuging, the port 5005 is available. The Osgi console is then reachable under http://localhost:8080/system/console/bundles.
If you need to fine tune the launching setup, you can modify the configuration of maven-pax-plugin according to the documention of [Pax Runner](https://ops4j1.jira.com/wiki/display/paxrunner/Pax+Runner)

Once the platform successfully launched, the single page application is accessivle under http://localhost:8080/obix/client/backbones/index.html




