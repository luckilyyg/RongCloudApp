package com.crazy.rongcloud;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

public class MainActivity extends AppCompatActivity {
    //super果 18673668974
    private static final String token1 ="JeXL+71vahbPjzSTYBlf3Okw/3FJenp53iTgy0iFgV+zWO2xI0jlx8+r479bFjga59uiwpcN87KhrP49wK/ZpQ==";
    //贝吉塔 18673668975
    private static final String token2 = "EacIz4+D+DRuqsNNIIPgxekw/3FJenp53iTgy0iFgV+zWO2xI0jlx4uF9kwNt6u3GIRIZqWHKKehrP49wK/ZpQ==";
    //希特 18673668976
    private static final String token3 = "pElfOoRbRK1Uka6WGUsmk+kw/3FJenp53iTgy0iFgV+zWO2xI0jlx9QozjjqpuGuVhvs9Cfvmj+hrP49wK/ZpQ==";

    private EditText ed;
    private String roomId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
    }

    private void initData() {
        findViewById(R.id.btn_chat_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //启动会话界面
                if (RongIM.getInstance() != null)
                    //为了更加直观，服务器建立连接后进入此界面，直接调用如下代码，
                    // 执行单人聊天，第二个参数代表对方用户ID，第三个参数代表聊天窗口标题，可以是对方的昵称
                    // 为了方便测试聊天，需要两个手机测试，所以登陆第一个token的用户与第二个用户"chao"建立聊天，
                    // 在运行第二个手机之前，记得改"chao"的token登录，然后聊天这里改为第一个的ID"text"。
                    //贝吉塔与super果聊天
                    RongIM.getInstance().startPrivateChat(MainActivity.this, "523846", "super果");
            }
        });
        findViewById(R.id.btn_chatlist_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectRongServer(token2);
                //启动会话列表
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
            }
        });

        RongIM.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
            @Override
            public boolean onReceived(Message message, int i) {
                return false;
            }
        });
        findViewById(R.id.btn_createGroup_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectRongServer(token1);
                //创建群聊
                final List<String> testList = new ArrayList<String>();
                testList.add("18673668975");
                testList.add("18673668976");
                testList.add("18673668977");
                RongIM.getInstance().createDiscussion("123", testList, new RongIMClient.CreateDiscussionCallback() {
                    @Override
                    public void onSuccess(String s) {
                        Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
                        ed = (EditText) findViewById(R.id.ed_id_main);
                        ed.setText(s);
                        roomId=s;
                    }
                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        //  Toast.makeText(MainActivity.this,errorCode.getValue()+"",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        //移除讨论组成员
        findViewById(R.id.btn_delgroup_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RongIM.getInstance().getRongIMClient().removeMemberFromDiscussion("bac45e36-3ef3-45b5-9047-dd25054c27ae", "10", new RongIMClient.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "移除成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        Toast.makeText(MainActivity.this, errorCode.getValue()+"", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //发送消息
        findViewById(R.id.btn_sendmessage_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 构造 TextMessage 实例
                TextMessage myTextMessage = TextMessage.obtain("消息");
                /* 生成 Message 对象。
                 * "7127" 为目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
                 * Conversation.ConversationType.PRIVATE 为私聊会话类型，根据需要，也可以传入其它会话类型，如群组，讨论组等。
                 *
                 */
                Message myMessage = Message.obtain("523846", Conversation.ConversationType.PRIVATE, myTextMessage);

                RongIM.getInstance().sendMessage(myMessage, null, null, new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {
                        //消息本地数据库存储成功的回调
                        Toast.makeText(MainActivity.this, "本地数据库存储成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Message message) {
                        //消息通过网络发送成功的回调
                        Toast.makeText(MainActivity.this, "网络发送成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        //消息发送失败的回调
                        Toast.makeText(MainActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    public void connectRongServer(String token) {
        if (getApplicationInfo().packageName.equals(App.getCurProcessName(getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误。可以从下面两点检查
                 * 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                 * 2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                 */
                @Override
                public void onTokenIncorrect() {
                    Log.d("RONGCLOUD", "--onTokenIncorrect");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {
                    Log.d("RONGCLOUD", "--onSuccess" + userid);

                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.d("RONGCLOUD", "--error" + errorCode.getMessage());
                }
            });
        }
    }


}
