## Kadecot�@��v���g�R��Plug-in�̃T���v��

Kadecot�ł́A�Ή��@���Plug-in�ɂ���Ċg�����邱�Ƃ��ł��܂��B
���̃v���W�F�N�g�́A����Plug-in�̃T���v���ł��B
Plug-in�̓������@�͒P���ŁAPlug-in��apk�t�@�C����Kadecot�Ɠ���[�����ɃC���X�g�[�����邾���ł��B

### Plug-in�̋N��

Plug-in�́A��{�I��Kadecot�{�̂��玩���I�ɋN�������悤�ɐݒ�ł��܂��B
AndroidManifest.xml��Service�v�f�̒��ɁA���L��Intent Filter��ݒ肷�邱�ƂŁAKadecot��Plug-in��F�����邱�Ƃ��ł��܂��B

    com.sonycsl.kadecot.plugin

����Intent Filter���ݒ肳��Ă���Service�́AKadecot��WebSocketServer�N�����Ɏ����I�ɋN������܂��B

�T���v���ł́A���̂悤�Ɏw�肵�Ă��܂��B

     <service android:name="SamplePluginService"
       android:exported="true">
       <intent-filter>
         <action android:name="com.sonycsl.kadecot.plugin" />
         <action android:name="com.sonycsl.Kadecot.plugin.sample.SamplePluginService" />
       </intent-filter>
     </service>


### Plug-in���̓o�^

�N�����ꂽPlug-in�́A�܂����g�̏���2��ɕ�����Kadecot�ɓo�^���܂��B
�����̏��͎�ɁAKadecot�{�̂�UI��Plug-in���𔽉f�����邽�߂Ɏ󂯓n���܂��B
1��ڂɓo�^������́A���̒ʂ�ł��B
 
* �T�|�[�g����v���g�R����
* Plug-in�̃p�b�P�[�W��
* �ݒ�pActivity��
 
�ݒ�pActivity���́AKadecot�{�̂�UI���璼�ڌĂяo�����̂ɗ��p����܂��B
���̂��߁AAndroidManifest.xml�ɂ́A
  
    android:exported="true"

�̃t���O���K�v�ł��B
�T���v���ł́A�ȉ��̂悤�ɂȂ��Ă��܂��B

    <activity
        android:name="com.sonycsl.Kadecot.plugin.sample.SettingsActivity"
        android:label="@string/activity_name" 
        android:exported="true">
    </activity>

2��ڂ̓o�^���́A���̒ʂ�ł��B

* �@��̎��
* �v���g�R����
* �@��̎�ނ�\���A�C�R��

�����̏����󂯓n�����ƂŁA�@�탊�X�g�ɋ@��̎�ނ�\���A�C�R�����o�����Ƃ��ł��܂��B

### WAMP(the Web Application Messaging Protocol)�ɂ��ʐM

Kadecot�{�̂�Plug-in�Ԃ̒ʐM�́A��{�I��WebSocket�̃T�u�v���g�R���ł���[WAMP](http://wamp.ws/)��p���čs���܂��B
Plug-in�́AWAMP��Client�Ƃ��ĐU�镑�����ƂŁAServer�ł���Kadecot�{�̂ƒʐM���邱�Ƃ��ł��܂��B
Plug-in���̓o�^���I���������ɁA����WAMP�ʐM���J�n���܂��B

WAMP�ʐM���n�߂邽�߂ɂ́A�܂�WebSocket���J���K�v������܂��B
Kadecot����WebSocket�T�[�o�́A�ȈՓI��origin���������Ă��邽�߁AKadecot���玩���N�����ꂽService��Intent�Ɋ܂܂��UUID���A
WebSocket���J���ۂ�origin�Ɋ܂߂Ȃ���΁AWebSocket���J�����Ƃ��ł��Ȃ��悤�ɂȂ��Ă��܂��B

WebSocket�AWAMP���C�u�����APlug-in�p��SDK�����L�̂悤�ɗp�ӂ��Ă���A
SDK���Ɋ܂܂��AKadecotProtocolClient���p�������N���X����������ƁA���������₷���Ȃ�܂��B
�T���v���ł́ASampleProtocolClient.java������ɂ�����܂��B

* WAMP�ʐM�p���C�u�����APlug-in�pSDK
 * java_websocket.jar
 * wamp.jar
 * kadecotclientsdk.jar


### WAMP�n���h�V�F�C�N����̏���

KadecotProtocolClient�ł́AWAMP��Hello/Welcome���b�Z�[�W��p�����n���h�V�F�C�N����������ƁA
�����I�Ɏ��̂��Ƃ��s���Ă���܂��B

 1. �V�X�e���Ƃ��ĕK�{��topic��Subscribe
 2. Plug-in�Ƃ��ė��p������topic��Subscribe
 3. Plug-in���T�|�[�g����procedure�̓o�^

1�ɂ́A�@�팟�����߂ł��鉺�L���܂܂�Ă���A

    com.sonycsl.kadecot.topic.private.search

����topic��Publish�����ƁA���̊֐����Ă΂��悤�ɂȂ��Ă��܂��B


    @Override
    public void onSearchEvent(WampEventMessage eventMsg) {
    }

���̊֐����ŁAPlug-in�ŗL�̋@�픭���������s���Ă��������B
�@�픭�������̌��ʁA�@�킪���������ꍇ��@���@��̏�Ԃ��ω������ꍇ�́A

    KadecotProtocolClient#registerDevice(DeviceData device)

���\�b�h���Ăяo���A
Kadecot�ɋ@���o�^���Ă��������B�@��̓o�^����������ƁA�@��ɑ΂���RPC�v���̎󂯎���Publish�ɂ����̔��s���\�ɂȂ�܂��B

�T���v���ł́A�@�픭�����߂��Ă΂ꂽ�Ƃ��ɋ����I�ɂQ�̋@���o�^����悤�ɂ��Ă��܂��B

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


2�́AgetTopicsToSubscribe���\�b�h�̖߂�l�Ŏw��ł��܂��B
�T���v���ł́A���Ɏw�肵�������̂��������߁A���L�̂悤�ɋ��Set��Ԃ��Ă��܂��B

    @Override
    public Set<String> getTopicsToSubscribe() {
        return new HashSet<String>();
    }

3�́AgetRegisterableProcedures���\�b�h�̖߂�l�Ŏw��ł��܂��B
�T���v���ł́A�S��procedure��o�^����悤�Ɏw�肵�Ă��܂��B

    com.sonycsl.kadecot.sample.procedure.procedure1
    com.sonycsl.kadecot.sample.procedure.procedure2
    com.sonycsl.kadecot.sample.procedure.testpublish
    com.sonycsl.kadecot.sample.procedure.echo

�Y���R�[�h�́A���̕����ł��B

    private static final String PROCEDURE = ".procedure.";

    private static final String DELAY_PUBLISH_TOPIC = PRE_FIX + TOPIC + "delaypublish";

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

    @Override
    public Map<String, String> getRegisterableProcedures() {
        Map<String, String> procs = new HashMap<String, String>();
        for (Procedure p : Procedure.values()) {
            procs.put(p.getUri(), p.getDescription());
        }
        return procs;
    }

### WAMP RPC���b�Z�[�W�̎󂯕t��

RPC�̗v�����s����ƁAKadecotProtocolClient#onInvocation()���Ăяo����܂��̂ŁA
procedure�ɑΉ������������s���Ă��������B

    protected WampMessage onInvocation(int requestId, String procedure, String uuid,
            JSONObject argumentsKw, WampInvocationReplyListener listener) {
    }


���s���ʂ́A�����Ƃ��ēn�����listener�̃��\�b�h���Ăяo�����ƂŁAYield�܂���Error���b�Z�[�W��Ԃ����Ƃ��ł��܂��B
�T���v���ł́A���L�̂悤�Ɍ��ʂ�Ԃ��Ă��܂��B

    @Override
    protected void onInvocation(int requestId, String procedure, String uuid,
            JSONObject argumentsKw, WampInvocationReplyListener listener) {

        try {
            final Procedure proc = Procedure.getEnum(procedure);
            if (proc == Procedure.ECHO) {
                listener.replyYield(WampMessageFactory.createYield(requestId, new JSONObject(),
                        new JSONArray(),
                        new JSONObject().put("text", argumentsKw.getString("text")))
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
        } c��atch (JSONException e) {
            listener.replyError(WampMessageFactory
                    .createError(WampMessageType.INVOCATION, requestId,
                            new JSONObject(), WampError.INVALID_ARGUMENT, new JSONArray(),
                            new JSONObject()).asErrorMessage());
        }
    }
   
### WAMP Publish�ɂ����̔��s

Publish�͒P���ŁA�C�ӂ̃^�C�~���O��

    KadecotProtocolClient#sendPublish(String uuid, String topic, JSONArray arguments, JSONObject argumentsKw)

���Ăяo���Ă��������B
�T���v���ł́A����Procedure���s�̂T�b���Publish���s����悤�ɐݒ肵�Ă��܂��B

    com.sonycsl.kadecot.sample.procedure.testpublish

���ۂɁAPublish�����topic�́A���̒ʂ�ł��B

    com.sonycsl.kadecot.sample.topic.delaypublish

�Y�������̃R�[�h�́A���L�̒ʂ�ł��B

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
