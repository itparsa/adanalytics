# ADAnalytics

 مورد نیاز برای استفاده از سرویس ادونت

##مراحل نصب

```groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
dependencies {
	        compile 'com.github.itparsa:adanalytics:1.0.0'
	}
```

## مراحل پیاده سازی 
ابتدا باید این موارد را در فایل AndroidManifest.xml قرار بدهید

```xml
<intent-filter>
                <data android:scheme="example" />
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
            </intent-filter>
            <!-- Branch App Links (optional) -->
            <intent-filter android:autoVerify="true">
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:scheme="https" android:host="example.com" />
                <data android:scheme="https" android:host="example" />
            </intent-filter>
            <intent-filter>
                <data android:scheme="example" android:host="open" />
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
            </intent-filter>
```

فقط به خاطر داشته باشید که این مقادیر باید در اکتیویتی لانچر شما قرار بگیرد
و در نظر داشته باشید که هر جا example هست را باید با اسم و یا دومین اپتون عوض کنید
 
 
 سپس این متا دیتا را در پایین تگ اپلیکیشن بگذارید و مقدار received key را با کلید دریافت شده از سرور ما عوض کنید

```xml
<Application
...

    <meta-data android:name="io.branch.sdk.BranchKey" android:value="received key" />

</application>
```

بعد از جاگذاری xml ها باید در کلاس application خود و در متد onCreate اس دی کی را اماده کنید
```java
public class App extends Application {
    
    @Override
        public void onCreate() {
            super.onCreate();
            ADAnalytics.initialize(this);
        }
    
} 
```

و در اخر هم در اکتیویتی لانچر باید این متد را ضدا بزنید


```java

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        ADAnalytics.countSession(this);
    }
}
```
و تمام