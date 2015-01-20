/*
 * Copyright (C) 2013-2014 Sony Computer Science Laboratories, Inc. All Rights Reserved.
 * Copyright (C) 2014 Sony Corporation. All Rights Reserved.
 */

package com.sonycsl.Kadecot.plugin.sample;

import android.os.Handler;

import com.sonycsl.Kadecot.plugin.DeviceData;
import com.sonycsl.Kadecot.plugin.KadecotProtocolClient;
import com.sonycsl.wamp.WampError;
import com.sonycsl.wamp.message.WampEventMessage;
import com.sonycsl.wamp.message.WampMessageFactory;
import com.sonycsl.wamp.message.WampMessageType;
import com.sonycsl.wamp.role.WampCallee.WampInvocationReplyListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SampleProtocolClient extends KadecotProtocolClient {
    static final String PROTOCOL_NAME = "sample";

    static final String DEVICE_TYPE_MEASURE = "measure";
    static final String DEVICE_TYPE_LIGHTING = "lighting";

    private static final String LOCALHOST = "127.0.0.1";

    private static final String PRE_FIX = "com.sonycsl.kadecot." + PROTOCOL_NAME;
    private static final String PROCEDURE = ".procedure.";
    private static final String TOPIC = ".topic.";

    private static final String DELAY_PUBLISH_TOPIC = PRE_FIX + TOPIC + "delaypublish";

    private Handler mHandler;

    public static enum Procedure {
        PROCEDURE1("procedure1", "http://example.plugin.explanation/procedure1"),
        PROCEDURE2("procedure2", "http://example.plugin.explanation/procedure2"),
        TESTPUBLISH("testpublish", "http://example.plugin.explanation/testpublish"),
        ECHO("echo", "http://example.plugin.explanation/echo"), ;

        private final String mUri;
        private final String mServiceName;
        private final String mDescription;

        /**
         * @param servicename
         * @param description is displayed on JSONP called /v
         */
        Procedure(String servicename, String description) {
            mUri = PRE_FIX + PROCEDURE + servicename;
            mServiceName = servicename;
            mDescription = description;
        }

        public String getUri() {
            return mUri;
        }

        public String getServiceName() {
            return mServiceName;
        }

        public String getDescription() {
            return mDescription;
        }

        public static Procedure getEnum(String procedure) {
            for (Procedure p : Procedure.values()) {
                if (p.getUri().equals(procedure)) {
                    return p;
                }
            }
            return null;
        }
    }

    public SampleProtocolClient() {
        mHandler = new Handler();
    }

    /**
     * Get the topics this plug-in want to SUBSCRIBE <br>
     */
    @Override
    public Set<String> getTopicsToSubscribe() {
        return new HashSet<String>();
    }

    /**
     * Get the procedures this plug-in supported. <br>
     */
    @Override
    public Map<String, String> getRegisterableProcedures() {
        Map<String, String> procs = new HashMap<String, String>();
        for (Procedure p : Procedure.values()) {
            procs.put(p.getUri(), p.getDescription());
        }
        return procs;
    }

    /**
     * Get the topics this plug-in supported. <br>
     */
    @Override
    public Map<String, String> getSubscribableTopics() {
        return new HashMap<String, String>();
    }

    private void searchDevice() {
        /**
         * Call after finding device.
         */
        registerDevice(new DeviceData.Builder(PROTOCOL_NAME, "sample_measure", DEVICE_TYPE_MEASURE,
                "samplePluginMeasure", true, LOCALHOST).build());
        registerDevice(new DeviceData.Builder(PROTOCOL_NAME, "sample_lighting",
                DEVICE_TYPE_LIGHTING, "samplePluginLighting", true, LOCALHOST).build());
    }

    @Override
    public void onSearchEvent(WampEventMessage eventMsg) {
        searchDevice();
    }

    @Override
    protected void onInvocation(final int requestId, String procedure, final String uuid,
            final JSONObject argumentsKw, final WampInvocationReplyListener listener) {

        try {
            final Procedure proc = Procedure.getEnum(procedure);
            if (proc == Procedure.ECHO) {
                listener.replyYield(WampMessageFactory.createYield(requestId, new JSONObject(),
                        new JSONArray(),
                        new JSONObject().put("text", argumentsKw.getString("text")))
                        .asYieldMessage());
                return;
            }

            if (proc == Procedure.TESTPUBLISH) {
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        sendPublish(uuid, DELAY_PUBLISH_TOPIC, new JSONArray(),
                                new JSONObject());
                    }
                }, 5000);

                listener.replyYield(WampMessageFactory.createYield(requestId, new JSONObject(),
                        new JSONArray(),
                        new JSONObject().put("result", "Do Publish after 5s"))
                        .asYieldMessage());
                return;
            }

            /**
             * Return YIELD message as a result of INVOCATION.
             */
            JSONObject argumentKw = new JSONObject().put("targetDevice", uuid).put(
                    "calledProcedure", procedure);

            listener.replyYield(WampMessageFactory.createYield(requestId, new JSONObject(),
                    new JSONArray(), argumentKw).asYieldMessage());
        } catch (JSONException e) {
            listener.replyError(WampMessageFactory
                    .createError(WampMessageType.INVOCATION, requestId,
                            new JSONObject(), WampError.INVALID_ARGUMENT, new JSONArray(),
                            new JSONObject()).asErrorMessage());
        }
    }
}
