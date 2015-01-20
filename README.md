## Kadecot機器プロトコルPlug-inのサンプル

Kadecotでは、対応機器をPlug-inによって拡張することができます。
このプロジェクトは、そのPlug-inのサンプルです。
Plug-inの導入方法は単純で、Plug-inのapkファイルをKadecotと同一端末内にインストールするだけです。

### Plug-inの起動

Plug-inは、基本的にKadecot本体から自動的に起動されるように設定できます。
AndroidManifest.xmlのService要素の中に、下記のIntent Filterを設定することで、KadecotはPlug-inを認識することができます。

    com.sonycsl.kadecot.plugin

このIntent Filterが設定されているServiceは、KadecotのWebSocketServer起動時に自動的に起動されます。

サンプルでは、次のように指定しています。

     <service android:name="SamplePluginService"
       android:exported="true">
       <intent-filter>
         <action android:name="com.sonycsl.kadecot.plugin" />
         <action android:name="com.sonycsl.Kadecot.plugin.sample.SamplePluginService" />
       </intent-filter>
     </service>


### Plug-in情報の登録

起動されたPlug-inは、まず自身の情報を2回に分けてKadecotに登録します。
これらの情報は主に、Kadecot本体のUIにPlug-in情報を反映させるために受け渡します。
1回目に登録する情報は、次の通りです。
 
* サポートするプロトコル名
* Plug-inのパッケージ名
* 設定用Activity名
 
設定用Activity名は、Kadecot本体のUIから直接呼び出されるのに利用されます。
そのため、AndroidManifest.xmlには、
  
    android:exported="true"

のフラグが必要です。
サンプルでは、以下のようになっています。

    <activity
        android:name="com.sonycsl.Kadecot.plugin.sample.SettingsActivity"
        android:label="@string/activity_name" 
        android:exported="true">
    </activity>

2回目の登録情報は、次の通りです。

* 機器の種類
* プロトコル名
* 機器の種類を表すアイコン

これらの情報を受け渡すことで、機器リストに機器の種類を表すアイコンを出すことができます。

### WAMP(the Web Application Messaging Protocol)による通信

Kadecot本体とPlug-in間の通信は、基本的にWebSocketのサブプロトコルである[WAMP](http://wamp.ws/)を用いて行います。
Plug-inは、WAMPのClientとして振る舞うことで、ServerであるKadecot本体と通信することができます。
Plug-in情報の登録が終わった直後に、このWAMP通信を開始します。

WAMP通信を始めるためには、まずWebSocketを開く必要があります。
Kadecot内のWebSocketサーバは、簡易的なorigin制限をしているため、Kadecotから自動起動されたServiceのIntentに含まれるUUIDを、
WebSocketを開く際のoriginに含めなければ、WebSocketを開くことができないようになっています。

WebSocket、WAMPライブラリ、Plug-in用のSDKを下記のように用意しており、
SDK内に含まれる、KadecotProtocolClientを継承したクラスを実装すると、実装がしやすくなります。
サンプルでは、SampleProtocolClient.javaがそれにあたります。

* WAMP通信用ライブラリ、Plug-in用SDK
 * java_websocket.jar
 * wamp.jar
 * kadecotclientsdk.jar


### WAMPハンドシェイク直後の処理

KadecotProtocolClientでは、WAMPのHello/Welcomeメッセージを用いたハンドシェイクが完了すると、
自動的に次のことを行ってくれます。

 1. システムとして必須なtopicのSubscribe
 2. Plug-inとして利用したいtopicのSubscribe
 3. Plug-inがサポートするprocedureの登録

1には、機器検索命令である下記が含まれており、

    com.sonycsl.kadecot.topic.private.search

このtopicがPublishされると、次の関数が呼ばれるようになっています。


    @Override
    public void onSearchEvent(WampEventMessage eventMsg) {
    }

この関数内で、Plug-in固有の機器発見処理を行ってください。
機器発見処理の結果、機器が見つかった場合や機器や機器の状態が変化した場合は、

    KadecotProtocolClient#registerDevice(DeviceData device)

メソッドを呼び出し、
Kadecotに機器を登録してください。機器の登録が完了すると、機器に対するRPC要求の受け取りやPublishによる情報の発行が可能になります。

サンプルでは、機器発見命令が呼ばれたときに強制的に２つの機器を登録するようにしています。

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


2は、getTopicsToSubscribeメソッドの戻り値で指定できます。
サンプルでは、特に指定したいものが無いため、下記のように空のSetを返しています。

    @Override
    public Set<String> getTopicsToSubscribe() {
        return new HashSet<String>();
    }

3は、getRegisterableProceduresメソッドの戻り値で指定できます。
サンプルでは、４つのprocedureを登録するように指定しています。

    com.sonycsl.kadecot.sample.procedure.procedure1
    com.sonycsl.kadecot.sample.procedure.procedure2
    com.sonycsl.kadecot.sample.procedure.testpublish
    com.sonycsl.kadecot.sample.procedure.echo

該当コードは、次の部分です。

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

### WAMP RPCメッセージの受け付け

RPCの要求が行われると、KadecotProtocolClient#onInvocation()が呼び出されますので、
procedureに対応した処理を行ってください。

    protected WampMessage onInvocation(int requestId, String procedure, String uuid,
            JSONObject argumentsKw, WampInvocationReplyListener listener) {
    }


実行結果は、引数として渡されるlistenerのメソッドを呼び出すことで、YieldまたはErrorメッセージを返すことができます。
サンプルでは、下記のように結果を返しています。

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
        } c￥atch (JSONException e) {
            listener.replyError(WampMessageFactory
                    .createError(WampMessageType.INVOCATION, requestId,
                            new JSONObject(), WampError.INVALID_ARGUMENT, new JSONArray(),
                            new JSONObject()).asErrorMessage());
        }
    }
   
### WAMP Publishによる情報の発行

Publishは単純で、任意のタイミングで

    KadecotProtocolClient#sendPublish(String uuid, String topic, JSONArray arguments, JSONObject argumentsKw)

を呼び出してください。
サンプルでは、次のProcedure実行の５秒後にPublishが行われるように設定しています。

    com.sonycsl.kadecot.sample.procedure.testpublish

実際に、Publishされるtopicは、次の通りです。

    com.sonycsl.kadecot.sample.topic.delaypublish

該当部分のコードは、下記の通りです。

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
