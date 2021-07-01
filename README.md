# IoT Enhanced Business Processes. Protegee plugin to facilitate the definition of high-level events

This repository contains a Protegee plugin to facilitate the creation of high-level events from low-level context data within the microservice architecture to support IoT-enhanced BPs available in the following Github repository: [iot-enhanced-business-process-infrastructure](https://github.com/pvalderas/iot-enhanced-business-process-infrastructure). According to this architecture, business microservices are in charge of managing IoT devices and publish low-level context data in an Event bus. A Context Monitor is in charge of analyzing this data and apply SPARQL rules in order to indentify and inject high-level events into a BP Controller microservice that is responsible of executing a BPMN model. 

The plugin of this repository facilitates the creation of the SPARQL rules from the context data published in the Event Bus. It is an extension of the SPARQL Tab plugin available in the follwing [Github repository](https://github.com/protegeproject/sparql-query-plugin)

# About

This is a contribution of a research work leaded by Pedro Valderas at the PROS Research Center, Universitat Politècnica de València, Spain.

This work presents a modelling approach based on BPMN and context ontologies to model IoT-enhanced BPs. This modelling approach is suppoted by a microservice architecture aimed at facilitating the integration of business processes with the physical world that provides high flexibility to support multiples IoT device technologies, and facilitates evolution and maintenance.

# Overview

This protegee plugin connectes to a Service Registry (e.g., Eureka) in which microservices that play the role of sensors are registered in order to obtain metadata about the context data published by the sensors. It also connect to the BP Controller that is in charge of executing an IoT-enhanced BP defined in a BPMN model in order to ask for the events that need to be injected from the phisucal world. Then, the plugin provides a user interface in order to facilitate the creation of SPARQL rules that generate high-level sensors by analysing the low-level context data.   

# The plugin in action

STEP 1: Selection of a IoT-enhanced BP

![step1](./step1.gif "Step 1")

STEP 2: Definition of SPARQL rules for the high-level events

![step2](./step2.gif "Step 2")

STEP 3: Testing the SPARQL rules 

![step3](./step31.gif "Step 3")

STEP 4: Map SPARQL rule with high-level event and deploying into the Context Monitor

![step4](./step41.gif "Step 4")