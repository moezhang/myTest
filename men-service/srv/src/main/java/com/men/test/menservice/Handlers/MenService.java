package com.men.test.menservice.Handlers;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;
import com.sap.cloud.sdk.datamodel.odata.helper.ModificationResponse;

import cds.gen.menservice.MenEntity;

import com.men.test.menservice.vdm.namespaces.ztestmoesrv.EducationInfo;
import com.men.test.menservice.vdm.namespaces.ztestmoesrv.EducationInfoCreateFluentHelper;
import com.men.test.menservice.vdm.namespaces.ztestmoesrv.SchoolInfo;
import com.men.test.menservice.vdm.namespaces.ztestmoesrv.SchoolInfoFluentHelper;
import com.men.test.menservice.vdm.namespaces.ztestmoesrv.field.SchoolInfoField;
import com.men.test.menservice.vdm.services.DefaultZTESTMOESRVService;
import com.men.test.menservice.vdm.services.ZTESTMOESRVService;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * MenService
 */
@Component
@ServiceName("MenService")
@ComponentScan({"com.sap.cloud.sdk", "com.men.test.menservice"})
@ServletComponentScan({"com.sap.cloud.sdk", "com.men.test.menservice", "com.men.test.menservice.vdm"})
public class MenService implements EventHandler {

    private Map<Object, Map<String, Object>> results = new HashMap<>();

    @On(event = CqnService.EVENT_READ, entity = "MenService.MenEntity")
    public void onRead(CdsReadEventContext context) {

        HttpDestination destination = DestinationAccessor.getDestination("s4hana").asHttp();

        DefaultZTESTMOESRVService moeService = new DefaultZTESTMOESRVService();
        List<SchoolInfo> schoolList = moeService.getAllSchoolInfoSet().executeRequest(destination);

        List<Map<String, Object>> result = new ArrayList<>();
        for (SchoolInfo schoolInfo : schoolList) {
            Map<String, Object> schoolData = Map.of(
                    "Schoolid", schoolInfo.getSchoolid(),
                    "Schoolname", schoolInfo.getSchoolname(),
                    "Location", schoolInfo.getLocation());
            result.add(schoolData);

            context.setResult(result);

        }
    }

    @On(event = CqnService.EVENT_CREATE, entity = "MenService.MenEntity")
    public void onCreate(CdsCreateEventContext context) {

        HttpDestination destination = DestinationAccessor.getDestination("s4hana").asHttp();
        DefaultZTESTMOESRVService moeService = new DefaultZTESTMOESRVService();

        Map<String, Object> recordIns = context.getCqn().entries().get(0);
        String schoolId = recordIns.get(MenEntity.SCHOOLID).toString();
        String schoolName = recordIns.get(MenEntity.SCHOOLNAME).toString();
        String location = recordIns.get(MenEntity.LOCATION).toString();

        SchoolInfo item = SchoolInfo.builder()
                .schoolid(schoolId)
                .schoolname(schoolName)
                .location(location)
                .build();

        ModificationResponse<SchoolInfo> createdSchool = moeService.createSchoolInfoSet(item)
                .executeRequest(destination);

        String test1 = "";

        MenEntity datamodel = com.sap.cds.Struct.create(MenEntity.class);
        Map<Object, Map<String, Object>> result = new HashMap<>();
        result.put("test", datamodel);

        context.setResult(result.values());

    }

}