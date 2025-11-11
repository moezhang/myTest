package com.sap.cap.productsservice.handlers;

 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Map;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import com.sap.cds.ResultBuilder;
import com.sap.cds.services.cds.CdsCreateEventContext;
 import com.sap.cds.services.cds.CdsReadEventContext;
 import com.sap.cds.services.cds.CqnService;
 import com.sap.cds.services.handler.EventHandler;
 import com.sap.cds.services.handler.annotations.On;
 import com.sap.cds.services.handler.annotations.ServiceName;

 import com.sap.cds.services.request.UserInfo;

import cds.gen.adminservice.Products;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cds.Result;
import com.sap.cds.ResultBuilder;

 //@SpringBootApplication
 @Component
 @ServiceName("AdminService")
 public class AdminService implements EventHandler {

     private Map<Object, Map<String, Object>> products = new HashMap<>();
     private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

     @On(event = CqnService.EVENT_CREATE, entity = "AdminService.Products")
     public void onCreate(CdsCreateEventContext context) {
         //context.getCqn().entries().forEach(e -> products.put(e.get("ID"), e));
         //context.setResult(context.getCqn().entries());

         Map<String, Object> recordIns = context.getCqn().entries().get(0);
         int inputId = Integer.parseInt(recordIns.get(Products.ID).toString());
         String title = recordIns.get(Products.TITLE).toString();
         String descr = recordIns.get(Products.DESCR).toString();

         logger.info("=== CREATE Products Event Started ===");
         logger.info("sapui5-id: {}", inputId);
         logger.info("sapui5-title: {}", title);
         logger.info("sapui5-descr: {}", descr);


         inputId = inputId + 10;
         title = title + "from CAP1";
         descr = descr + "from CAP2";

         Map<Object, Map<String, Object>> result = new HashMap<>();
         Products datamodel = com.sap.cds.Struct.create(Products.class);

         datamodel.put(Products.ID, inputId);
         datamodel.put(Products.TITLE, title);
         datamodel.put(Products.DESCR, descr);
         result.put("result", datamodel);

         Result r = ResultBuilder.selectedRows(Collections.singletonList(datamodel)).result();
         logger.info("=== ResultPreview: {} ===", r);
         context.setResult(r);

     }

     @On(event = CqnService.EVENT_READ, entity = "AdminService.Products")
     public void onRead(CdsReadEventContext context) {
        
        Products datamodel = com.sap.cds.Struct.create(Products.class);

        UserInfo user = context.getUserInfo();
        datamodel.put(Products.TITLE, user.getName());
        datamodel.put(Products.DESCR, "from CAP backend!");

         Map<Object, Map<String, Object>> result = new HashMap<>();
         String keyTemp = "111";
         result.put(keyTemp, datamodel);
         context.setResult(result.values());
     }

 }