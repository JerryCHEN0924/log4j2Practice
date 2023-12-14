## 一、Log簡介
作為一個完整的軟體，日誌是必不可少的。
程序從開發、測試、維護、運行等環節，都需要向控制台或文件等地方輸出大量訊息。這些訊息的輸出，在很多時候是使用 System.out.println() 無法完成的。
日誌訊息根據用途與記錄內容的不同，可分為：調試日誌、運行日誌、異常日誌等。
## 簡介
首先介紹幾種常見的log框架有
### log4j
Log4j 的全稱為 Log for java，即專門用於 Java 語言的日誌記錄工具，也是Java中最早且最廣泛使用的日誌框架之一。
Log4j有三個主要的元件：Loggers、Appenders、Layouts。

**Loggers(寫Log)**
被分為五個級別：DEBUG < INFO < WARN < ERROR < FATAL
只輸出級別不低於設定級別的訊息
ex：Loggers為INFO，則INFO、WARN、ERROR和FATAL都會輸出 DEBUG則不會

**Appenders(log訊息的終點(目的地))**
常使用的類如下：
org.apache.log4j.ConsoleAppender（控制台）
org.apache.log4j.FileAppender（文件）
org.apache.log4j.DailyRollingFileAppender（每天產生一個日誌文件）
org.apache.log4j.RollingFileAppender（文件大小到達指定size的時候產生一個新的文件）
org.apache.log4j.WriterAppender（將日誌訊息以輸出流格式發送到任意指定的地方）
如 System.out.println("Some message..."), 對Log4j 而言appenders 就是 console

**Layouts(Log訊息的格式)**
常使用的類如下：
org.apache.log4j.HTMLLayout（以HTML表格形式佈局）
org.apache.log4j.PatternLayout（可以靈活地指定佈局模式）
org.apache.log4j.SimpleLayout（包含日誌訊息的級別和訊息字符串）
org.apache.log4j.TTCCLayout（包含日誌產生的時間、執行緒、類別等訊息）
它提供了靈活的配置選項，可以透過配置檔案 (properties 或 XML) 或程式碼設定來定義日誌輸出的格式、等級等。
過程式碼中記錄日誌時，使用 Logger 介面來完成。
雖然 log4j 在它的時間內是非常流行且有效的，但在更近期的版本中已經停止了更新，請改使用log4j2。

### log4j2
log4j2 是 log4j 的後繼版本，它在設計上解決了一些 log4j 存在的性能和低效率問題。
它提供了異步日誌記錄機制，可以更有效地處理大量的日誌輸出。
log4j2 也支援配置文件和程式碼方式來進行日誌的配置。
與 log4j 相比，log4j2 在性能方面有顯著的改進，且在設計上更現代化。

### slf4j
slf4j 不是一個實際的日誌實作框架，而是一個日誌的抽象層（Facade）。
它提供了一個通用的日誌介面，使得程式碼可以與特定的日誌實作（如 log4j、log4j2、java.util.logging 等）解耦，從而達到更容易切換不同的日誌實作的目的。
使用 slf4j，您的應用程式會依賴 slf4j API，而實際的日誌實作則可以在部署時進行配置。

### Commons Logging
它與 slf4j 的作用類似，也是提供了一個抽象的日誌介面，讓程式碼和日誌實作解耦。
Commons Logging 是 Apache Commons 專案提供的日誌介面。
在common-logging中，有一個Simple logger的簡單實現，但是它功能很弱，所以使用common-logging，通常都是配合著log4j來使用；
Commons Logging 使用的是 "發現式" 的日誌實作方式，即在執行時尋找可用的日誌實作來處理輸出。
然而，因為 Commons Logging 的設計問題，有時候可能會引起類載入的問題，而在**新的應用程式中通常更推薦使用 slf4j**。

**小小結論:**slf4j 是使用得最廣泛且推薦的日誌抽象層，而 log4j2 是在性能方面較優秀的具體日誌實作。

## slf4j+log4j2
使用maven，首先在pom.xml中加入以下依賴
```
        <dependency>
			<groupId>org.apache.logging.log4j</groupId>
			<artifactId>log4j-api</artifactId>
			<version>2.9.1</version>
		</dependency>
		<dependency>
			<groupId>org.apache.logging.log4j</groupId>
			<artifactId>log4j-core</artifactId>
			<version>2.9.1</version>
		</dependency>
		<!--與slf4j保持橋接-->
		<dependency>
			<groupId>org.apache.logging.log4j</groupId>
			<artifactId>log4j-slf4j-impl</artifactId>
			<version>2.11.0</version>
		</dependency>
		<!-- slf4j核心包-->
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-api</artifactId>
			<version>1.7.25</version>
		</dependency>
```
```
+-Configuration
    +-Properties
    +-Appenders:
        +-Appender
            +-Layout
            +-Policies
            +-Strategy
    +-Loggers
        +-Logger
        +-RootLogger
```
根節點Configuration有兩個屬性：status和monitorinterval：
1. status：用來指定log4j本身的打印日誌的級別；
2. monitorinterval：用於指定log4j2自動重新配置的監測間隔時間，單位是秒，最小是5秒。

### 節點說明：
1. Properties：一般用來做屬性定義；
2. Appender：可以理解為一個管道，定義了日誌內容的輸出位置：
**Console：用來定義輸出到控制台的Appender；**
name：指定Appender的名稱；
target：SYSTEM_OUT或SYSTEM_ERR，一般只設置默認值：SYSTEM_OUT；
PatternLayout：輸出格式，不設置默認值為：%m%n；Filter：配置日誌事件能否被輸出。過濾條件有三個值：ACCEPT(接受)，DENY(拒絕)，NEUTRAL(中立)；

**File：用來定義輸出到指定位置的文件Appender；**
name：指定Appender的名稱；
fileName：指定輸出日誌的目的文件帶全路徑的文件名；
PatternLayout：輸出格式，不設置默認值為：%m%n；
Filter：配置日誌事件能否被輸出。過濾條件有三個值：ACCEPT(接受)，DENY(拒絕)，NEUTRAL(中立)；

**RollingFile：用來定義超過指定大小自動刪除舊的創建新文件的Appender；**
name：指定Appender的名稱；
fileName：指定輸出日誌的目的文件帶全路徑的文件名；
PatternLayout：輸出格式，不設置默認值為：%m%n；
Filter：配置日誌事件能否被輸出。過濾條件有三個值：ACCEPT(接受)，DENY(拒絕)，NEUTRAL(中立)；
PatternLayout：輸出格式，不設置默認值為：%m%n；
Policies：指定滾動日誌的策略，就是什麼時候進行新建日誌文件輸出日誌；
Strategy：配置Strategy以控制日誌如何(How)進行滾動。

3. Loggers：簡單說Logger就是一個路由器，指定類、包中的日誌訊息流向哪個Appender，以及控制他們的日誌級別；

**Root：必須要配置；用來指定項目的根日誌，如果沒有單獨指定Logger，那麼就會默認使用該Root日誌輸出；**
level：屬性；用來指定日誌輸出級別；
AppenderRef：Root的子節點，用來指定該日誌輸出到哪個Appender。

**Logger：用來單獨指定日誌的形式，比如要為指定包下的class指定不同的日誌級別等。**

level：屬性；用來指定日誌輸出級別；
name：屬性；用來指定該Logger所適用的類或者類所在的包全路徑，繼承自Root節點；
AppenderRef：Logger的子節點，用來指定該日誌輸出到哪個Appender；如果沒有指定，就會默認繼承自Root；如果指定了，那麼會在指定的這個Appender和Root的Appender中都會輸出，此時我們可以設置Logger的additivity="false"只在自定義的Appender中進行輸出。

**上面提到的Filter一般常用的有以下三種：**
LevelRangeFilter：輸出指定日誌級別範圍之內的日誌；
TimeFilter：在指定時間範圍內才會輸出日誌；
ThresholdFilter：輸出符合特定日誌級別及其以上級別的日誌。

**上面提到的Policies一般常用的有以下三種：**

SizeBasedTriggeringPolicy：根據日誌文件的大小進行滾動；單位有：KB，MB，GB；
CronTriggeringPolicy：使用Cron表達式進行日誌滾動，很靈活；
TimeBasedTriggeringPolicy：這個配置需要和filePattern結合使用，注意filePattern中配置的文件重命名規則。滾動策略依賴於filePattern中配置的最具體的時間單位，根據最具體的時間單位進行滾動。這種方式比較簡潔。
### xml配置
```
<?xml version="1.0" encoding="UTF-8"?>
<!--Configuration後面的status，這個用於設置log4j2自身內部的信息輸出，可以不設置，當設置成trace時，你會看到log4j2內部各種詳細輸出-->
<!--monitorInterval：Log4j能夠自動檢測修改配置文件和重新配置本身，設置間隔秒數-->
<configuration monitorInterval="5">
    <!--日誌級別以及優先級排序: OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE > ALL -->

    <!--變量配置-->
    <Properties>
        <!-- 格式化輸出：%date表示日期，%thread表示線程名，%-5level：級別從左顯示5個字符寬度 %msg：日誌消息，%n是換行符-->
        <!-- %logger{36} 表示 Logger 名字最長36個字符 -->
        <property name="LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n" />
        <!-- 定義日誌存儲的路徑，不要配置相對路徑 -->
        <property name="FILE_PATH" value="E:\logs\log4j2" />
        <property name="FILE_NAME" value="springbootlog4j2" />
    </Properties>

    <appenders>

        <console name="Console" target="SYSTEM_OUT">
            <!--輸出日誌的格式-->
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <!--控制台只輸出level及其以上級別的信息（onMatch），其他的直接拒絕（onMismatch）-->
            <ThresholdFilter level="info" onMatch="ACCEPT" onMismatch="DENY"/>
        </console>

        <!--文件會打印出所有信息，這個log每次運行程序會自動清空，由append屬性決定，適合臨時測試用-->
        <File name="Filelog" fileName="${FILE_PATH}/test.log" append="false">
            <PatternLayout pattern="${LOG_PATTERN}"/>
        </File>

        <!-- 這個會打印出所有的info及以下級別的信息，每次大小超過size，則這size大小的日誌會自動存入按年份-月份建立的文件夾下面並進行壓縮，作為存檔-->
        <RollingFile name="RollingFileInfo" fileName="${FILE_PATH}/info.log" filePattern="${FILE_PATH}/${FILE_NAME}-INFO-%d{yyyy-MM-dd}_%i.log.gz">
            <!--控制台只輸出level及以上級別的信息（onMatch），其他的直接拒絕（onMismatch）-->
            <ThresholdFilter level="info" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <!--interval屬性用來指定多久滾動一次，默认是1 hour-->
                <TimeBasedTriggeringPolicy interval="1"/>
                <SizeBasedTriggeringPolicy size="10MB"/>
            </Policies>
            <!-- DefaultRolloverStrategy屬性如不設置，則默認為最多同一文件夾下7個文件開始覆蓋-->
            <DefaultRolloverStrategy max="15"/>
        </RollingFile>

        <!-- 這個會打印出所有的warn及以下級別的信息，每次大小超過size，則這size大小的日誌會自動存入按年份-月份建立的文件夾下面並進行壓縮，作為存檔-->
        <RollingFile name="RollingFileWarn" fileName="${FILE_PATH}/warn.log" filePattern="${FILE_PATH}/${FILE_NAME}-WARN-%d{yyyy-MM-dd}_%i.log.gz">
            <!--控制台只輸出level及以上級別的信息（onMatch），其他的直接拒絕（onMismatch）-->
            <ThresholdFilter level="warn" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <!--interval屬性用來指定多久滾動一次，默认是1 hour-->
                <TimeBasedTriggeringPolicy interval="1"/>
                <SizeBasedTriggeringPolicy size="10MB"/>
            </Policies>
            <!-- DefaultRolloverStrategy屬性如不設置，則默認為最多同一文件夾下7個文件開始覆蓋-->
            <DefaultRolloverStrategy max="15"/>
        </RollingFile>

        <!-- 這個會打印出所有的error及以下級別的信息，每次大小超過size，則這size大小的日誌會自動存入按年份-月份建立的文件夾下面並進行壓縮，作為存檔-->
        <RollingFile name="RollingFileError" fileName="${FILE_PATH}/error.log" filePattern="${FILE_PATH}/${FILE_NAME}-ERROR-%d{yyyy-MM-dd}_%i.log.gz">
            <!--控制台只輸出level及以上級別的信息（onMatch），其他的直接拒絕（onMismatch）-->
            <ThresholdFilter level="error" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <!--interval屬性用來指定多久滾動一次，默认是1 hour-->
                <TimeBasedTriggeringPolicy interval="1"/>
                <SizeBasedTriggeringPolicy size="10MB"/>
            </Policies>
            <!-- DefaultRolloverStrategy屬性如不設置，則默認為最多同一文件夾下7個文件開始覆蓋-->
            <DefaultRolloverStrategy max="15"/>
        </RollingFile>

    </appenders>

    <!--Logger節點用來單獨指定日誌的形式，比如要為指定包下的class指定不同的日誌級別等。-->
    <!--然後定義loggers，只有定義了logger並引入的appender，appender才會生效-->
    <loggers>

        <!--過濾掉spring和mybatis的一些無用的DEBUG信息-->
        <logger name="org.mybatis" level="info" additivity="false">
            <AppenderRef ref="Console"/>
        </logger>
        <!--監控系統信息-->
        <!--若是additivity設為false，則 子Logger 只會在自己的appender裡輸出，而不會在 父Logger 的appender裡輸出。-->
        <Logger name="org.springframework" level="info" additivity="false">
            <AppenderRef ref="Console"/>
        </Logger>

        <root level="info">
            <appender-ref ref="Console"/>
            <appender-ref ref="Filelog"/>
            <appender-ref ref="RollingFileInfo"/>
            <appender-ref ref="RollingFileWarn"/>
            <appender-ref ref="RollingFileError"/>
        </root>
    </loggers>
</configuration>
```


參考資料來源:
https://segmentfault.com/a/1190000037598528
https://www.dataset.com/blog/maven-log4j2-project/
https://hackmd.io/GuX8ngltT2yUWHWeb5UV-g
https://ithelp.ithome.com.tw/articles/10261299
https://killerbryant.pixnet.net/blog/post/44215393
https://dotblogs.azurewebsites.net/Leon-Yang/2021/01/06/155519
https://kknews.cc/zh-tw/code/yz9arja.html
