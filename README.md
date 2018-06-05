# MVP + RxJava + OKHttp3 设计打车软件
> [接口文档](https://www.easyapi.com/api/?documentId=8067&themeId=&categoryId=13213)

## 最简单的MVP实现
思路:

 1. 定义一个接口,用来规定针对这个界面逻辑View需要做出的动作的接口
 
 ```java
 public interface IView {
 
     /**
      * 显示loading
      */
     void showLoading();
 
     /**
      * 显示错误
      */
     void showError(int Code, String msg);
 
 }
 ```
 
 ```java
 public interface ICreatePasswordDialogView extends IView {
 
     /**
      * 显示注册成功
      */
     void showRegisterSuc();
 
     /**
      * 显示登录成功
      */
     void showLoginSuc();
 
     /**
      * 显示密码为空
      */
     void showPasswordNull();
 
     /**
      * 显示两次密码输入不一致
      */
     void showPasswordNotEqual();
 
 }
 ```
 2. 让Activity , Dialog , popuWindow  等 UI层实现接口的方法,也就是 V 层
 ```xml
 <?xml version="1.0" encoding="utf-8"?>
 <android.support.v7.widget.LinearLayoutCompat xmlns:android="http://schemas.android.com/apk/res/android"
                                               xmlns:tools="http://schemas.android.com/tools"
                                               android:orientation="vertical"
                                               android:layout_width="match_parent"
                                               android:layout_height="wrap_content">
 
     <RelativeLayout
             android:layout_width="match_parent"
             android:layout_height="wrap_content">
 
         <android.support.v7.widget.AppCompatImageView
                 android:layout_width="wrap_content"
                 android:layout_height="wrap_content"
                 android:src="@drawable/btn_close"
                 android:layout_alignParentRight="true"
                 android:padding="@dimen/activity_vertical_margin"
                 android:id="@+id/close"
                 />
     </RelativeLayout>
 
     <android.support.v7.widget.LinearLayoutCompat
             android:padding="@dimen/activity_horizontal_margin"
             android:orientation="vertical"
             android:layout_width="match_parent"
             android:layout_height="wrap_content">
 
         <android.support.v7.widget.AppCompatTextView
                 android:layout_width="match_parent"
                 android:layout_height="wrap_content"
                 android:text="@string/create_pw"
                 android:id="@+id/dialog_title"
                 style="@style/Dialog.Title"
                 android:gravity="center_horizontal"
                 />
 
         <android.support.v7.widget.AppCompatTextView
                 android:layout_width="match_parent"
                 android:layout_height="wrap_content"
                 style="@style/Dialog.Input"
                 android:id="@+id/phone"
                 tools:text="你的手机号为：15919496914"
                 android:gravity="center_horizontal"
                 />
 
         <android.support.v7.widget.AppCompatEditText
                 android:layout_width="match_parent"
                 android:layout_height="wrap_content"
                 android:hint="@string/pls_input_pw"
                 style="@style/Dialog.Input"
                 android:id="@+id/pw"
                 android:inputType="textPassword"
                 android:textColor="@color/color_text_normal"
                 />
 
         <android.support.v7.widget.AppCompatEditText
                 android:layout_width="match_parent"
                 android:layout_height="wrap_content"
                 android:hint="@string/pls_input_pw_again"
                 style="@style/Dialog.Input"
                 android:id="@+id/pw1"
                 android:inputType="textPassword"
                 android:textColor="@color/color_text_normal"
                 />
 
         <android.support.v4.widget.ContentLoadingProgressBar
                 android:layout_width="wrap_content"
                 android:layout_height="wrap_content"
                 android:id="@+id/loading"
                 android:layout_gravity="center_horizontal"
                 style="@style/Loading"
                 android:visibility="gone"
                 />
 
         <android.support.v7.widget.AppCompatTextView
                 android:layout_width="match_parent"
                 android:layout_height="wrap_content"
                 tools:text="错误"
                 android:gravity="center"
                 android:textColor="@color/error_red"
                 android:id="@+id/tips"
                 android:visibility="gone"
                 android:padding="12dp"
                 style="@style/Tips"
                 />
     </android.support.v7.widget.LinearLayoutCompat>
 
     <android.support.v7.widget.AppCompatButton
             android:layout_width="match_parent"
             android:layout_height="wrap_content"
             android:text="@string/confirm"
             style="@style/Dialog.Button"
             android:id="@+id/btn_confirm"
             />
 
 </android.support.v7.widget.LinearLayoutCompat>
 ```
 
 ```java
 public class CreatePasswordDialog extends Dialog implements ICreatePasswordDialogView{
     private TextView mPhone;
     private Button mBtnConfirm;
     private View mLoading;
     private TextView mPw;
     private TextView mPw1;
     private TextView mTips;
     private String mPhoneStr;
     private ICreatePasswordDialogPresenter mPresenter;
     private Object mContext;
 
     public CreatePasswordDialog(Context context, String phone) {
         this(context, R.style.Dialog);
         // 上一个页面传来的手机号
         mPhoneStr = phone;
         IHttpClient httpClient = new OkHttpClientImpl();
         SharedPreferencesDao dao =
                 new SharedPreferencesDao(MyTaxiApplication.getInstance(),
                         SharedPreferencesDao.FILE_ACCOUNT);
         IAccountManager accountManager =  new AccountManagerImpl(httpClient, dao);
         mPresenter = new CreatePasswordDialogPresenterImpl(this, accountManager);
         mContext = context;
 
     }
 
 
     public CreatePasswordDialog(Context context, int theme) {
         super(context, theme);
 
     }
 
     @Override
     public void onDetachedFromWindow() {
         super.onDetachedFromWindow();
 
     }
 
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         LayoutInflater inflater = (LayoutInflater) getContext()
                 .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         View root = inflater.inflate(R.layout.dialog_create_pw, null);
         setContentView(root);
         initViews();
 
         //注册 presenter
         RxBus.getInstance().register(mPresenter);
     }
 
     @Override
     public void dismiss() {
         super.dismiss();
         // 注销 presenter
         RxBus.getInstance().unRegister(mPresenter);
     }
 
 
     private void initViews() {
         mPhone = (TextView) findViewById(R.id.phone);
         mPw = (EditText) findViewById(R.id.pw);
         mPw1 = (EditText) findViewById(R.id.pw1);
         mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
         mLoading = findViewById(R.id.loading);
         mTips = (TextView) findViewById(R.id.tips);
         findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 dismiss();
             }
         });
         mBtnConfirm.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 submit();
             }
         });
         mPhone.setText(mPhoneStr);
 
     }
 
 
 
     /**
      * 提交注册
      */
     private void submit() {
         String password = mPw.getText().toString();
         String password1 = mPw1.getText().toString();
         boolean check = mPresenter.checkPw(password, password1);
         if (check) {
             mPresenter.requestRegister(mPhoneStr, password1);
         }
     }
 
     @Override
     public void showPasswordNull() {
         mTips.setVisibility(View.VISIBLE);
         mTips.setText(getContext().getString(R.string.password_is_null));
         mTips.setTextColor(getContext().
                 getResources().getColor(R.color.error_red));
     }
 
     @Override
     public void showPasswordNotEqual() {
         mTips.setVisibility(View.VISIBLE);
         mTips.setText(getContext()
                 .getString(R.string.password_is_not_equal));
         mTips.setTextColor(getContext()
                 .getResources().getColor(R.color.error_red));
     }
 
     /**
      *  注册成功
      */
     @Override
     public void showRegisterSuc() {
 
         mLoading.setVisibility(View.VISIBLE);
         mBtnConfirm.setVisibility(View.GONE);
         mTips.setVisibility(View.VISIBLE);
         mTips.setTextColor(getContext()
                 .getResources()
                 .getColor(R.color.color_text_normal));
         mTips.setText(getContext()
                 .getString(R.string.register_suc_and_loging));
         // 请求网络，完成自动登录
         mPresenter.requestLogin(mPhoneStr, mPw.getText().toString());
     }
     /**
      *  登录成功
      */
     @Override
     public void showLoginSuc() {
         dismiss();
         ToastUtil.show(getContext(),
                 getContext().getString(R.string.login_suc));
         if (mContext instanceof MainActivity) {
             ((MainActivity)mContext).showLoginSuc();
         }
     }
 
     /**
      * 请求中 loading
      */
     @Override
     public void showLoading() {
         showOrHideLoading(true);
     }
 
     /**
      * 错误提示
      * @param code
      * @param msg
      */
     @Override
     public void showError(int code, String msg) {
         showOrHideLoading(false);
         switch (code) {
             case IAccountManager.PW_ERROR:
                 showLoginFail();
                 break;
             case IAccountManager.SERVER_FAIL:
                 showServerError();
                 break;
         }
     }
 
 
     private void showOrHideLoading(boolean show) {
         if (show) {
             mLoading.setVisibility(View.VISIBLE);
             mBtnConfirm.setVisibility(View.GONE);
         } else {
             mLoading.setVisibility(View.GONE);
             mBtnConfirm.setVisibility(View.VISIBLE);
         }
 
     }
 
 
     private void showLoginFail() {
         dismiss();
         ToastUtil.show(getContext(),
                 getContext().getString(R.string.error_server));
     }
 
     private void showServerError() {
         mTips.setTextColor(getContext()
                 .getResources().getColor(R.color.error_red));
         mTips.setText(getContext().getString(R.string.error_server));
     }
 }
 ```
 3. 创建一个类,用来封装之前的网络请求过程,也就是 M 层
 ```java
 public class BaseBizResponse {
 
     public static final int STATE_OK = 200;
     // 密码错误
     public static final int STATE_PW_ERR = 100005;
     // token 无效／过期
     public static final int STATE_TOKEN_INVALID = 100006;
     // 用户已经存在
     public static int STATE_USER_EXIST = 100003;
     // 用户不存在
     public static int STATE_USER_NOT_EXIST = 100002;
     // 状态码
     private int code;
     private String msg;
 
     public int getCode() {
         return code;
     }
 
     public void setCode(int code) {
         this.code = code;
     }
 
     public String getMsg() {
         return msg;
     }
 
     public void setMsg(String msg) {
         this.msg = msg;
     }
 }
 ```
 
 ```java
 public class RegisterResponse extends BaseBizResponse {
 }

 ```
 4. 再创建一个类,用来处理 M 层 和 V 层的通信,也就是P层
 ```java
 public interface ICreatePasswordDialogPresenter {
     /**
      * 校验密码输入合法性
      */
     boolean checkPw(String pw, String pw1);
     /**
      *  提交注册
      */
     void requestRegister(String phone, String pw);
 
     /**
      * 登录
      */
     void requestLogin(String phone, String pw);
 }
 ```
 
 ```java
 public class CreatePasswordDialogPresenterImpl implements ICreatePasswordDialogPresenter {
 
 
     private ICreatePasswordDialogView view;
     private IAccountManager accountManager;
 
     @RegisterBus
     public void onRegisterResponse(RegisterResponse registerResponse) {
         // 处理UI 变化
         switch (registerResponse.getCode()) {
             case IAccountManager.REGISTER_SUC:
                 // 注册成功
                 view.showRegisterSuc();
                 break;
             case IAccountManager.LOGIN_SUC:
                 // 登录成功
                  view.showLoginSuc();
                 break;
             case IAccountManager.SERVER_FAIL:
                 // 服务器错误
                view.showError(IAccountManager.SERVER_FAIL, "");
                 break;
         }
 
     }
 
     @RegisterBus
     public void onLoginResponse(LoginResponse LoginResponse) {
         // 处理UI 变化
         switch (LoginResponse.getCode()) {
             case IAccountManager.LOGIN_SUC:
                 // 登录成功
                 view.showLoginSuc();
                 break;
             case IAccountManager.SERVER_FAIL:
                 // 服务器错误
                 view.showError(IAccountManager.SERVER_FAIL, "");
                 break;
         }
 
     }
 
 
     /**
      * 注入 view 和 accountManager 对象
      *
      * @param view
      * @param accountManager
      */
     public CreatePasswordDialogPresenterImpl(ICreatePasswordDialogView view,
                                              IAccountManager accountManager) {
         this.view = view;
         this.accountManager = accountManager;
 
     }
 
     /**
      * 校验密码输入合法性
      *
      * @param pw
      * @param pw1
      */
     @Override
     public boolean checkPw(String pw, String pw1) {
         if (pw == null || pw.equals("")) {
 
             view.showPasswordNull();
             return false;
         }
         if (!pw.equals(pw1)) {
 
             view.showPasswordNotEqual();
             return false;
         }
        return true;
 
     }
 
     /**
      * 注册
      *
      * @param phone
      * @param pw
      */
     @Override
     public void requestRegister(String phone, String pw) {
 
         accountManager.register(phone, pw);
     }
 
     /**
      * 登录
      *
      * @param phone
      * @param pw
      */
     @Override
     public void requestLogin(String phone, String pw) {
 
         accountManager.login(phone, pw);
     }
 }

 ```
 
这就是: MVP的整体架构实现
 
 <img src="https://github.com/MicroKibaco/Taxi/blob/master/doc/mvp.png" width="800" height="400" />
 
 好了,上面4步就是基本的MVP模式的使用了
 
## 章节一: 启动过渡页面开发
### A. SVG 用法
SVG 有何优点?
- SVG 可被非常多的工具读取和修改

- SVG 与 JPEG 和 GIF 图像比起来,尺寸更小,可压缩性更强

 <img src="https://github.com/MicroKibaco/Taxi/blob/master/doc/icon_logo.png" width="100" height="100" />
   
### B. SVG 制造logo
- M = moveTo (M X,Y) : 将画笔移动到指定的坐标位置

- L = lineTo(L X,Y) : 画直线到指定的坐标位置

- H = horizontal lineTo(H X) : 画水平线到指定的X坐标位置

- V = vertical lineTo(V Y) : 画垂直线到指定的Y坐标位置

- A = elliptical Arc(A RX, RY, XROTATION, FLAG1, FLAG2,X,Y) : 弧线

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
        android:width="50dp"
        android:height="72dp"
        android:viewportWidth="50.0"
        android:viewportHeight="72.0">
    <path
        android:name="logo"
        android:strokeColor="@color/logoColor"
        android:strokeWidth="10"
        android:pathData="M5,10,v50,L20,60,A1,1,0,0,0,20,15,L0,15"/>
</vector>
```

 ```java
  final AnimatedVectorDrawable anim = (AnimatedVectorDrawable) getResources()
                    .getDrawable(R.drawable.anim);
            mLogo.setImageDrawable(anim);
            anim.start();
 ```

 <img src="https://github.com/MicroKibaco/Taxi/blob/master/doc/splash.png" width="368" height="667" />


## 章节二: OKHttp3 的使用

- 为什么要选择 OkHttp

- OkHttp 的使用

- Get/Post 请求

- 拦截器
```
拦截器就是所有的请求和所有的响应,它都能截获到,
统计请求花费的时间和日志,拦截器是一种面向切面 IOP的思想
```
- 缓存


## 章节三: 基础网络模块—架构实现及OkHttp封装
 - 关系图
 
 <img src="https://github.com/MicroKibaco/Taxi/blob/master/doc/net-relation.png" width="600" height="300" />
 
 - 怎么做封装?
 
 <img src="https://github.com/MicroKibaco/Taxi/blob/master/doc/baseOkHttp.png" width="1000" height="300" />

- Http 协议
- OKHttp 的优点与用法
- 封装一个App 网络库的方法(分层思想)

## 章节四: 用户登录模块
 <img src="https://github.com/MicroKibaco/Taxi/blob/master/doc/login.png" width="736" height="1334" />

 
- 定义 MVP 各层接口
- 抽离网络请求数据存储过程, 实现 M 层
- 整理 View 层,形成被动视图, 实现 P 层

### MVC , MVP ,MVVM 架构对比
 <img src="https://github.com/MicroKibaco/Taxi/blob/master/doc/mvc-mvp-mvvm.png" width="1000" height="300" />


