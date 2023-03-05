Ptoceti : an OSGI R6 Iot Gatway
==================================================

Ptoceti is a set of Osgi bundles to build a Iot Gateway

Ptoceti is build on top of [Osgi](www.osgi.org) R6 with use of Device Access, Device Abstraction Layer and Device Abstraction Layer Function specifications.


## Ptoceti's modules

Ptoceti's main modules are:

- com.ptoceti.osgi.configadmin.eventlistener : a bundle exposing Configuration Admin event throught Event Admin
- com.ptoceti.osgi.control :
- com.ptoceti.osgi.deviceaccess : a implementation of Osgi DeviceAccess specification
- com.ptoceti.osgi.deviceadmin : 
- com.ptoceti.osgi.ebus : ebus driver exposed throught Device Access Specification
- com.ptoceti.osgi.ebusdevice : ebus device exposed throught Device Abstract Layer and Function specification
- com.ptoceti.osgi.feature : feature bundle to expose all bundle as features in Karaf
- com.ptoceti.osgi.ihm : L'ihm en Angular 10
- com.ptoceti.osgi.modbus : modbus driver exposed throught Device Access Specification
- com.ptoceti.osgi.modbusdevice : modbus device exposed throught Device Abstract Layer and Function specification
- com.ptoceti.osgi.mqtt : an mqtt client based on Mosquitto
- com.ptoceti.osgi.rest : une api rest pour l'ihm en jaxrs 2.1 via RestEasy
- com.ptoceti.osgi.resteasy : parent module for RestEasy bundle
- com.ptoceti.osgi.resteasy-core : exposition of RestEasy Core as an Osgi Bundle
- com.ptoceti.osgi.resteasy-core-spi : exposition of RestEasy Core Spi as an Osgi Bundle
- com.ptoceti.osgi.resteasy-guice : exposition of RestEasy Guice as an Osgi Bundle
- com.ptoceti.osgi.resteasy-jackson2-provider : exposition of RestEasy Jackson2 as an Osgi Bundle
- com.ptoceti.osgi.resteasy-jaxb-provider: exposition of RestEasy Jaxb as an Osgi Bundle
- com.ptoceti.osgi.serialdevice.nrjjavaserial : a serial device factory according to Serial Device Specification via NrjJavaSerial
- com.ptoceti.osgi.serialdevice.rxtx : a serial device factory according to Serial Device Specification via Rxtx
- com.ptoceti.osgi.smallrye : Jakarte type configuration needed by RestEasy
- com.ptoceti.osgi.timeseries : A time serie listener based on InfluxDb
- com.ptoceti.osgi.usb.api : exposition of javax.usb.api as a bundle
- com.ptoceti.osgi.usb4java : parant module for usb4java
- com.ptoceti.osgi.usb4java.usb4java : exposition of org.usb4java as an osgi bundle
- com.ptoceti.osgi.usb4java.usb4java-javax : exposition of or.usb4java-javax as an osgi bundle
- com.ptoceti.osgi.usbdevice : a usb device factory according to USB Information Device Category specification   
- com.ptoceti.osgi.wireadmin : a implementation of Osgi WireAdmin specification



## Compatibility and requirements

Ptoceti is build with Java 11.

It makes uses of the following specs in Osgi R6:
- Log Service Specification
- Http Service Specification
- Device Access Specification
- Configuration Admin Service Specification
- Metatype Service Specification
- Wire Admin Service Specification
- Event Admin Service Specification
- Http Whiteboard Specification
- Device Abstraction Layer Specification
- Device Abstraction Layer Functions Specification
- USB Information Device Category Specification
- Serial Device Service Specification
- Position Specification
- Measurement and State Specification








