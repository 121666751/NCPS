package com.adtec.http;


import org.apache.commons.httpclient.HttpException;
import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;
import org.apache.log4j.Logger;

/* *
 *类名：HttpProtocolHandler
 *功能：HttpClient方式访问
 *详细：获取远程HTTP数据
 */

public class HttpProtocolHandler {
    private static String              DEFAULT_CHARSET                     = "UTF-8";

    /** 连接超时时间，由bean factory设置，缺省为8秒钟 */
    private int                        defaultConnectionTimeout            = 8000;

    /** 回应超时时间, 由bean factory设置，缺省为30秒钟 */
    private int                        defaultSoTimeout                    = 30000;

    /** 闲置连接超时时间, 由bean factory设置，缺省为60秒钟 */
    private int                        defaultIdleConnTimeout              = 60000;

    private int                        defaultMaxConnPerHost               = 30;

    private int                        defaultMaxTotalConn                 = 80;

    /** 默认等待HttpConnectionManager返回连接超时（只有在达到最大连接数时起作用）：1秒*/
    private static final long          defaultHttpConnectionManagerTimeout = 3 * 1000;

    /**
     * HTTP连接管理器，该连接管理器必须是线程安全的.
     */
    private HttpConnectionManager      connectionManager;

    private static HttpProtocolHandler httpProtocolHandler                 = new HttpProtocolHandler();

    /**
     * 工厂方法
     * 
     * @return
     */
    public static HttpProtocolHandler getInstance() {
        return httpProtocolHandler;
    }

    /**
     * 私有的构造方法
     */
    public HttpProtocolHandler() {
        // 创建一个线程安全的HTTP连接池
        connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(defaultMaxConnPerHost);
        connectionManager.getParams().setMaxTotalConnections(defaultMaxTotalConn);

        IdleConnectionTimeoutThread ict = new IdleConnectionTimeoutThread();
        ict.addConnectionManager(connectionManager);
        ict.setConnectionTimeout(defaultIdleConnTimeout);

        ict.start();
    }
    
    public static void main(String[] args) throws HttpException, IOException {
    	HttpProtocolHandler handler = new HttpProtocolHandler();
    	
    	 /*String root = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><MsgHeader><MsgVar>001</MsgVar><SndDt>2015年</SndDt><Trxtyp>1</Trxtyp><IssrId>10150</IssrId><Drctn>1</Drctn><SignSN>1</SignSN><EncSN>1</EncSN><EncKey>545556</EncKey><MDAlgo>1</MDAlgo><SignEncAlgo>1</SignEncAlgo><EncAlgo>1</EncAlgo><EncAlgo>1</EncAlgo></MsgHeader>" +
    			"<MsgBody><BizTp>1</BizTp>" +
    			"<TrxInf><TrxId>111</TrxId><TrxDtTm>20171010101023</TrxDtTm><SettlmtDt>2017202020</SettlmtDt><TrxAmt>100.00</TrxAmt><RPFlg>2</RPFlg></TrxInf>" +
    			"<RcverInf><SgnNo>UP110201707050836481000000141</SgnNo><RcverAcctIssrId>1</RcverAcctIssrId><RcverAcctId>6200008888</RcverAcctId><RcverNm>123456</RcverNm>" +
    			"<IDTp>1</IDTp><IDNo>429004198410110931</IDNo><MobNo>13588888888</MobNo><Smskey>20170613100000164</Smskey><AuthMsg>123456</AuthMsg><RcverAcctTp>1</RcverAcctTp>" +
    			"</RcverInf><SensInf>1</SensInf>" +
    			"<SderInf><SderIssrId>10167</SderIssrId><SderAcctIssrId>20160</SderAcctIssrId></SderInf>" +
    			"<RskInf><deviceMode>1</deviceMode><deviceLanguage>1</deviceLanguage><sourceIP>1</sourceIP><MAC>1</MAC><devId>1</devId><extensiveDeviceLocation>1</extensiveDeviceLocation><deviceNumber></deviceNumber>" +
    			"<deviceSIMNumber>1</deviceSIMNumber><accountIDHash>1</accountIDHash><riskScore>twr</riskScore><riskReasonCode>1</riskReasonCode><mchntUsrRgstrTm>1</mchntUsrRgstrTm><mchntUsrRgstrEmail>1</mchntUsrRgstrEmail>" +
    			"<rcvProvince>1</rcvProvince><rcvCity>1</rcvCity><goodsClass></goodsClass></RskInf>" +
    			"<OrdrInf><OrdrId>46444</OrdrId></OrdrInf>" +
    			"<PyerInf><AuthMsg>123456</AuthMsg><Smskey>20170613100000023</Smskey><PyerAcctIssrId>10167</PyerAcctIssrId><IDTp>1</IDTp>" +
    			"<IDNo>429004198410110931</IDNo><MobNo>13588888888</MobNo><SgnNo>UP110201707050836481000000141</SgnNo><PyerAcctId>6200008888</PyerAcctId>" +
    			"<PyerAcctTp>00</PyerAcctTp><PyeeIssrId>1016000</PyeeIssrId>"+
    			"</PyerInf>"+
    			"<PyeeInf><PyeeAcctIssrId>22323</PyeeAcctIssrId><PyeeAcctId>810188888888</PyeeAcctId><PyeeIssrId></PyeeIssrId>10160000</PyeeInf>"+
    			"<SubMrchntInf><SubMrchntNo>00001</SubMrchntNo></SubMrchntInf>"+
    			"<ProductInf><ProductTp>QR030000</ProductTp></ProductInf>"+
    			"<ChannelIssrInf><SgnNo>UP110201707050836481000000141</SgnNo></ChannelIssrInf>"+
    			"<OriTrxInf><OriTrxId>100000238</OriTrxId><OriTrxAmt>5000.00</OriTrxAmt><OrdrId>000000123</OrdrId><OriTrxDtTm>20171010101023</OriTrxDtTm></OriTrxInf>"+
    			"</MsgBody></root>";
    			*/
    	//0001
    	/*
    	String root = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
    			+ "<root><MsgHeader><MsgVer>1000</MsgVer><SndDt>2017-05-21T16:18:07</SndDt><Trxtyp>0001</Trxtyp><IssrId>XXXXXXXX</IssrId><Drctn>11</Drctn>"
    			+ "<SignSN>274881513107</SignSN><EncSN>4000370671</EncSN>"
    			+ "<EncKey>gXaV1L/NO6a4cJ3g562cYD8kNlOoKedlvcnJqmBkFmnrO49AHcrshHlWF54AFHAj4diDjwzhM4ToUj85mUHsQKW8dYhHbdpQmXmFj2zQ3tEgijw3KHzDtRopaONOlTb0uaPt074HpvgPkP/hONnT0YWGAPbmRv/zrZDrfCfqkbKiwI6uIt5avhDAssMRokHvwx3aMDDaa7Lt5p+K0lDdl5twm84U4F8DRqzOeJwGoJ7vVbsymn9wkIS8mkR0vcCqM4OcYZdj7y7L5BU4sUypuwu8nN61DSl7o+lXP9/ocmmLG7sX/Z4HWpE+LaPRgl35jm+tSpPgHf+QOx1WapRs+Q==</EncKey>"
    			+ "<MDAlgo>0</MDAlgo><SignEncAlgo>0</SignEncAlgo><EncAlgo>0</EncAlgo></MsgHeader><MsgBody><BizTp>100003</BizTp><TrxInf><TrxId>0521161807723940</TrxId>"
    			+ "<TrxDtTm>2017-05-21T16:18:07</TrxDtTm><SettlmtDt></SettlmtDt></TrxInf><RcverInf><RcverAcctIssrId></RcverAcctIssrId><RcverAcctId>621214390002000****</RcverAcctId>"
    			+ "<RcverNm>123456</RcverNm><IDTp>01</IDTp><IDNo>429004198410100584</IDNo><MobNo>13581792068</MobNo></RcverInf><SensInf>CgEqkE6Hfk8EwU1vdMDQ1ESnUY/SXMHBteARQQkpDdD3cb9+N53S1Q==</SensInf>"
    			+ "<SderInf><SderIssrId>0102</SderIssrId><SderAcctIssrId>XXXXXXXX</SderAcctIssrId></SderInf><CorpCard><CorpName></CorpName><USCCode></USCCode></CorpCard>"
    			+ "<ProductInf><ProductTp></ProductTp><ProductAssInformation></ProductAssInformation></ProductInf><RskInf><deviceMode></deviceMode><deviceLanguage>"
    			+ "</deviceLanguage><sourceIP></sourceIP><MAC></MAC><devId></devId><extensiveDeviceLocation></extensiveDeviceLocation>"
    			+ "<deviceNumber></deviceNumber><deviceSIMNumber></deviceSIMNumber><accountIDHash></accountIDHash><riskScore></riskScore><riskReasonCode>"
    			+ "</riskReasonCode><mchntUsrRgstrTm></mchntUsrRgstrTm><mchntUsrRgstrEmail></mchntUsrRgstrEmail><rcvProvince></rcvProvince><rcvCity></rcvCity>"
    			+ "<goodsClass></goodsClass></RskInf></MsgBody></root>{S:g4GrrY92Du1Np81QPiI1Y4l+3RvUgS7iAXSGhJPtMBbKH6KNrAh79UdiksWBRkLq5G9IUFVbU0D26BqiY3wpXMIvc1NedcXkJj0+XE0FfeUYvlX3ZiN5421qxRpZsxh1XhiOMPHTEfjGlkP2bJl37KenES5KLvDamPUR1xiXRLFKWavrdw4HG+WB6ApmtUdreJ4WGyZFe1GP1sz7+CI5CI3Cf58+2GjQGbjSnw9IAoHd5TaqQqCvkXlmqklg5nLhTcdEAwTL4zzBJpbhtLsYtmJBEaNfiocyHkZSoVRGyJZTGl2lHQcWl3lTJcnIG9QRYDZRX9ZKaLtW78JdTMWLvQ==}";
    	*/
    	//0301
    	//String root = "<root><MsgHeader><MsgVer>1000</MsgVer><SndDt>2017-05-19T11:27:23</SndDt><Trxtyp>0301</Trxtyp><IssrId>XXXXXXXX</IssrId><Drctn>11</Drctn><SignSN>UP0100010201708020616221000000484</SignSN><EncSN></EncSN><EncKey></EncKey><MDAlgo>0</MDAlgo><SignEncAlgo>0</SignEncAlgo><EncAlgo></EncAlgo></MsgHeader><MsgBody><BizTp>100003</BizTp><TrxInf><TrxId>1705191275646698</TrxId><TrxDtTm>2017-05-19T11:27:23</TrxDtTm><SettlmtDt></SettlmtDt></TrxInf><RcverInf><RcverAcctIssrId></RcverAcctIssrId><RcverAcctId>628888888888****</RcverAcctId><RcverAcctTp></RcverAcctTp><RcverNm></RcverNm><IDTp>01</IDTp><IDNo>429004198410110930</IDNo><SgnNo>UP0100010201708020616221000000484</SgnNo></RcverInf><SderInf><SderIssrId>XXXXXXXX</SderIssrId><SderAcctIssrId>XXXXXXXX</SderAcctIssrId></SderInf><RskInf><deviceMode></deviceMode><deviceLanguage></deviceLanguage><sourceIP></sourceIP><MAC></MAC><devId></devId><extensiveDeviceLocation></extensiveDeviceLocation><deviceNumber></deviceNumber><deviceSIMNumber></deviceSIMNumber><accountIDHash></accountIDHash><riskScore></riskScore><riskReasonCode></riskReasonCode><mchntUsrRgstrTm></mchntUsrRgstrTm><mchntUsrRgstrEmail></mchntUsrRgstrEmail><rcvProvince></rcvProvince><rcvCity></rcvCity><goodsClass></goodsClass></RskInf></MsgBody></root>";
    	//0201
    	//String root = "<root><MsgHeader><MsgVer>1000</MsgVer><SndDt>2017-05-21T17:13:48</SndDt><Trxtyp>0201</Trxtyp><IssrId>XXXXXXXX</IssrId><Drctn>11</Drctn><SignSN>274881513107</SignSN><EncSN></EncSN><EncKey></EncKey><MDAlgo>0</MDAlgo><SignEncAlgo>0</SignEncAlgo><EncAlgo></EncAlgo></MsgHeader><MsgBody><BizTp>100002</BizTp><TrxInf><TrxId>0521171348666369</TrxId><TrxDtTm>2017-05-21T17:13:48</TrxDtTm><SettlmtDt></SettlmtDt></TrxInf><RcverInf><RcverAcctTp>01</RcverAcctTp><RcverAcctIssrId>0100</RcverAcctIssrId><RcverAcctId>621214390002000****</RcverAcctId><RcverNm>*********</RcverNm><IDTp>01</IDTp><IDNo>429004198410110930</IDNo><MobNo>13581792068</MobNo><AuthMsg>261907</AuthMsg><Smskey>20170613100000475</Smskey></RcverInf><SensInf></SensInf><SderInf><SderIssrId>XXXXXXXX</SderIssrId><SderAcctIssrId>XXXXXXXX</SderAcctIssrId></SderInf><OriTrxInf><OriTrxId>0521144726302964</OriTrxId></OriTrxInf><CorpCard><CorpName></CorpName><USCCode></USCCode></CorpCard><ProductInf><ProductTp></ProductTp><ProductAssInformation></ProductAssInformation></ProductInf><RskInf><deviceMode></deviceMode><deviceLanguage></deviceLanguage><sourceIP></sourceIP><MAC></MAC><devId></devId><extensiveDeviceLocation></extensiveDeviceLocation><deviceNumber></deviceNumber><deviceSIMNumber></deviceSIMNumber><accountIDHash></accountIDHash><riskScore></riskScore><riskReasonCode></riskReasonCode><mchntUsrRgstrTm></mchntUsrRgstrTm><mchntUsrRgstrEmail></mchntUsrRgstrEmail><rcvProvince></rcvProvince><rcvCity></rcvCity><goodsClass></goodsClass></RskInf></MsgBody></root>{S:vZ8Muvll5r3T/Rv9CVow1l0F8ONIlJvLFxpcg8Y5t4sI2F/WUvMdupbymoH2v8ijgXQbzn/NOzcJjahRxEuQEmXde25tnNNPJRU/MYr1mfcuQE+HiS/CQ4r+CkJ7TkKgHu6WpTp0bKTA7aRJ1Y3TmgOwf2QjO/wMumR65XOJNQzhFOo6VajpoF/ZPrH+tdx3ZER8P8s1hH8iXxCGIPDy9H9CAPr5Zf96m6ZxrIzZG71OJIhrbD3jWysg1uLDy8SOiOnlxqSrnkjfIdGQH9b1XITWo+ZENPFTACsMtnsWRa2yx+WY93wbbA8VxlDavx3+++pbrdnkTn02tnko76JAnw==}";
    	//1001
    	//String root = "<root><MsgHeader><MsgVer>1000</MsgVer><SndDt>2017-05-19T14:21:35</SndDt><Trxtyp>1001</Trxtyp><IssrId>XXXXXXXX</IssrId><Drctn>11</Drctn><SignSN>274881513107</SignSN><EncSN></EncSN><EncKey></EncKey><MDAlgo>0</MDAlgo><SignEncAlgo>0</SignEncAlgo><EncAlgo></EncAlgo></MsgHeader><MsgBody><BizTp>100003</BizTp><TrxInf><TrxId>1705199212860124</TrxId><TrxDtTm>2017-05-19T14:21:35</TrxDtTm><SettlmtDt></SettlmtDt><TrxAmt>CNY100.00</TrxAmt><AcctInTp></AcctInTp><TrxTrmTp>07</TrxTrmTp><TrxTrmNo></TrxTrmNo><RPFlg>1</RPFlg></TrxInf><PyerInf><PyeeIssrId>6666</PyeeIssrId><PyerAcctIssrId>7777</PyerAcctIssrId><PyerAcctId>621214390002000****</PyerAcctId><PyerAcctTp></PyerAcctTp><PyerNm></PyerNm><IDTp></IDTp><IDNo></IDNo><MobNo></MobNo></PyerInf><SensInf></SensInf><PyeeInf><PyeeIssrId>9999</PyeeIssrId><SderIssrId>XXXXXXXX</SderIssrId><PyeeAcctIssrId>XXXXXXXX</PyeeAcctIssrId><PyeeAcctId></PyeeAcctId><PyeeNm></PyeeNm><PyeeAreaNo></PyeeAreaNo></PyeeInf><ResfdInf><ResfdAcctIssrId></ResfdAcctIssrId><InstgAcctId></InstgAcctId><InstgAcctNm></InstgAcctNm></ResfdInf><ChannelIssrInf><ChannelIssrId></ChannelIssrId><SgnNo>UP0100010201708020616221000000484</SgnNo></ChannelIssrInf><ProductInf><ProductTp></ProductTp><ProductAssInformation></ProductAssInformation></ProductInf><OrdrInf><OrdrId>1705199212860124</OrdrId><OrdrDesc></OrdrDesc></OrdrInf><MrchntInf><MrchntNo>865920248160001</MrchntNo><MrchntTpId>4816</MrchntTpId><MrchntPltfrmNm>CPCN</MrchntPltfrmNm></MrchntInf><SubMrchntInf><SubMrchntNo></SubMrchntNo><SubMrchntTpId></SubMrchntTpId><SubMrchntPltfrmNm></SubMrchntPltfrmNm></SubMrchntInf><RskInf><deviceMode></deviceMode><deviceLanguage></deviceLanguage><sourceIP>127.0.0.1</sourceIP><MAC>4039445</MAC><devId>5656</devId><extensiveDeviceLocation>44747</extensiveDeviceLocation><deviceNumber></deviceNumber><deviceSIMNumber></deviceSIMNumber><accountIDHash></accountIDHash><riskScore></riskScore><riskReasonCode></riskReasonCode><mchntUsrRgstrTm></mchntUsrRgstrTm><mchntUsrRgstrEmail></mchntUsrRgstrEmail><rcvProvince></rcvProvince><rcvCity></rcvCity><goodsClass></goodsClass></RskInf></MsgBody></root>"
    	//		+ "{S:RdSSjiJRRIrHy6DH641e7b/lNw35qFdaBDd7SM5eEmy/vv+NQWCaaqFfnR9Txi0r19fv56vDnk8rjvwPOgt6xwqOSqH2Di87qpqzZWhOGq3v+871Dr9JRQXK9X3Fx/jtzAmFkVrFwKu3PiqZjiAKp8UX2sONy26jJtp+nV15Hgmo1MemtxZ78pUhPSEKMarZVe5QjnTTcSKm7oBiNtpG2rJai48bPloiQSzmg7FCRt5R6NOx6U1R8kOn7CfMYT4TuIatTaAPBaeF1CeNe2IViUX0I3P6G/C+q64nvsN/jdnP1MOJM8OyDGLJQdTHKUd93+JUMHmT6c0mbNGTP7ghBA==}";
    	//1002
    	//String root = "<root><MsgHeader><MsgVer>1000</MsgVer><SndDt>2017-05-21T16:49:29</SndDt><Trxtyp>1002</Trxtyp><IssrId>XXXXXXXX</IssrId><Drctn>11</Drctn><SignSN>274881513107</SignSN><EncSN></EncSN><EncKey></EncKey><MDAlgo>0</MDAlgo><SignEncAlgo>0</SignEncAlgo><EncAlgo>1</EncAlgo></MsgHeader><MsgBody><BizTp>100003</BizTp><TrxInf><TrxId>1705214634970482</TrxId><TrxDtTm>2017-05-21T16:49:29</TrxDtTm><SettlmtDt></SettlmtDt><TrxAmt>CNY100.00</TrxAmt><AcctInTp></AcctInTp><TrxTrmTp>07</TrxTrmTp><TrxTrmNo></TrxTrmNo><RPFlg></RPFlg></TrxInf><PyerInf><PyerAcctIssrId></PyerAcctIssrId><PyerAcctId>621214300000000****</PyerAcctId><PyerAcctTp></PyerAcctTp><PyerNm></PyerNm><IDTp></IDTp><IDNo></IDNo><MobNo></MobNo><AuthMsg>111111</AuthMsg><Smskey>2017052114472630</Smskey></PyerInf><SensInf></SensInf><PyeeInf><PyeeIssrId>XXXXXXXX</PyeeIssrId><PyeeAcctIssrId>XXXXXXXX</PyeeAcctIssrId><PyeeAcctId></PyeeAcctId><PyeeNm></PyeeNm><PyeeAreaNo></PyeeAreaNo></PyeeInf><ResfdInf><ResfdAcctIssrId></ResfdAcctIssrId><InstgAcctId></InstgAcctId><InstgAcctNm></InstgAcctNm></ResfdInf><ProductInf><ProductTp></ProductTp><ProductAssInformation></ProductAssInformation></ProductInf><OrdrInf><OrdrId>1705214634970482</OrdrId><OrdrDesc></OrdrDesc></OrdrInf><MrchntInf><MrchntNo>865920248160001</MrchntNo><MrchntTpId>4816</MrchntTpId><MrchntPltfrmNm>CPCN</MrchntPltfrmNm></MrchntInf><SubMrchntInf><SubMrchntNo></SubMrchntNo><SubMrchntTpId></SubMrchntTpId><SubMrchntPltfrmNm></SubMrchntPltfrmNm></SubMrchntInf><RskInf><deviceMode></deviceMode><deviceLanguage></deviceLanguage><sourceIP></sourceIP><MAC></MAC><devId></devId><extensiveDeviceLocation></extensiveDeviceLocation><deviceNumber></deviceNumber><deviceSIMNumber></deviceSIMNumber><accountIDHash></accountIDHash><riskScore></riskScore><riskReasonCode></riskReasonCode><mchntUsrRgstrTm></mchntUsrRgstrTm><mchntUsrRgstrEmail></mchntUsrRgstrEmail><rcvProvince></rcvProvince><rcvCity></rcvCity><goodsClass></goodsClass></RskInf></MsgBody></root>";
    	//1101
    	//String root = "<root><MsgHeader><MsgVer>1000</MsgVer><SndDt>2017-05-22T15:43:15</SndDt><Trxtyp>1101</Trxtyp><IssrId>0100</IssrId><Drctn>11</Drctn><SignSN>274881513107</SignSN><EncSN></EncSN><EncKey></EncKey><MDAlgo>0</MDAlgo><SignEncAlgo>0</SignEncAlgo><EncAlgo></EncAlgo></MsgHeader><MsgBody><BizTp>100003</BizTp><TrxInf><TrxId>1705223946702856</TrxId><TrxDtTm>2017-05-22T15:43:15</TrxDtTm><SettlmtDt></SettlmtDt><TrxAmt>CNY0.10</TrxAmt><AcctInTp></AcctInTp><TrxTrmTp>07</TrxTrmTp><TrxTrmNo></TrxTrmNo><RPFlg>2</RPFlg></TrxInf><PyeeInf><PyeeAcctIssrId>7777</PyeeAcctIssrId><PyeeAcctId>628888888888****</PyeeAcctId></PyeeInf><PyerInf><PyeeIssrId>9999</PyeeIssrId><PyerIssrId>0100</PyerIssrId><PyerAcctIssrId>XXXXXXXX</PyerAcctIssrId></PyerInf><ResfdInf><ResfdAcctIssrId></ResfdAcctIssrId><InstgAcctId></InstgAcctId><InstgAcctNm></InstgAcctNm></ResfdInf><ChannelIssrInf><ChannelIssrId></ChannelIssrId><SgnNo>UP66666666XXXXXXXX020XXXXXXXX66666666201705190000350173</SgnNo></ChannelIssrInf><MrchntInf><MrchntNo>865920248160001</MrchntNo><MrchntTpId>4816</MrchntTpId><MrchntPltfrmNm>CPCN</MrchntPltfrmNm></MrchntInf><SubMrchntInf><SubMrchntNo></SubMrchntNo><SubMrchntTpId></SubMrchntTpId><SubMrchntPltfrmNm></SubMrchntPltfrmNm></SubMrchntInf><OriTrxInf><OriTrxId>1705199212860124</OriTrxId><OriTrxAmt>CNY0.10</OriTrxAmt><OriOrdrId>1705225912919706</OriOrdrId><OriTrxDtTm>2017-05-19T14:21:35</OriTrxDtTm><ProductTp></ProductTp><ProductAssInformation></ProductAssInformation></OriTrxInf><RskInf><deviceMode></deviceMode><deviceLanguage></deviceLanguage><sourceIP></sourceIP><MAC></MAC><devId></devId><extensiveDeviceLocation></extensiveDeviceLocation><deviceNumber></deviceNumber><deviceSIMNumber></deviceSIMNumber><accountIDHash></accountIDHash><riskScore></riskScore><riskReasonCode></riskReasonCode><mchntUsrRgstrTm></mchntUsrRgstrTm><mchntUsrRgstrEmail></mchntUsrRgstrEmail><rcvProvince></rcvProvince><rcvCity></rcvCity><goodsClass></goodsClass></RskInf></MsgBody></root>";
    	//2001
    	String root = "<root><MsgHeader><MsgVer>1000</MsgVer><SndDt>2017-05-22T15:43:15</SndDt><Trxtyp>1101</Trxtyp><IssrId>0100</IssrId><Drctn>11</Drctn><SignSN>274881513107</SignSN><EncSN></EncSN><EncKey></EncKey><MDAlgo>0</MDAlgo><SignEncAlgo>0</SignEncAlgo><EncAlgo></EncAlgo></MsgHeader><MsgBody><BizTp>100003</BizTp><TrxInf><TrxId>1705223946702856</TrxId><TrxDtTm>2017-05-22T15:43:15</TrxDtTm><SettlmtDt></SettlmtDt><TrxAmt>CNY0.10</TrxAmt><AcctInTp></AcctInTp><TrxTrmTp>07</TrxTrmTp><TrxTrmNo></TrxTrmNo><RPFlg>2</RPFlg></TrxInf><PyeeInf><PyeeAcctIssrId>7777</PyeeAcctIssrId><PyeeAcctId>628888888888****</PyeeAcctId></PyeeInf><PyerInf><PyeeIssrId>9999</PyeeIssrId><PyerIssrId>0100</PyerIssrId><PyerAcctIssrId>XXXXXXXX</PyerAcctIssrId></PyerInf><ResfdInf><ResfdAcctIssrId></ResfdAcctIssrId><InstgAcctId></InstgAcctId><InstgAcctNm></InstgAcctNm></ResfdInf><ChannelIssrInf><ChannelIssrId></ChannelIssrId><SgnNo>UP66666666XXXXXXXX020XXXXXXXX66666666201705190000350173</SgnNo></ChannelIssrInf><MrchntInf><MrchntNo>865920248160001</MrchntNo><MrchntTpId>4816</MrchntTpId><MrchntPltfrmNm>CPCN</MrchntPltfrmNm></MrchntInf><SubMrchntInf><SubMrchntNo></SubMrchntNo><SubMrchntTpId></SubMrchntTpId><SubMrchntPltfrmNm></SubMrchntPltfrmNm></SubMrchntInf><OriTrxInf><OriTrxId>1705199212860124</OriTrxId><OriTrxAmt>CNY0.10</OriTrxAmt><OriOrdrId>1705225912919706</OriOrdrId><OriTrxDtTm>2017-05-19T14:21:35</OriTrxDtTm><ProductTp></ProductTp><ProductAssInformation></ProductAssInformation></OriTrxInf><RskInf><deviceMode></deviceMode><deviceLanguage></deviceLanguage><sourceIP></sourceIP><MAC></MAC><devId></devId><extensiveDeviceLocation></extensiveDeviceLocation><deviceNumber></deviceNumber><deviceSIMNumber></deviceSIMNumber><accountIDHash></accountIDHash><riskScore></riskScore><riskReasonCode></riskReasonCode><mchntUsrRgstrTm></mchntUsrRgstrTm><mchntUsrRgstrEmail></mchntUsrRgstrEmail><rcvProvince></rcvProvince><rcvCity></rcvCity><goodsClass></goodsClass></RskInf></MsgBody></root>{S:vZ8Muvll5r3T/Rv9CVow1l0F8ONIlJvLFxpcg8Y5t4sI2F/WUvMdupbymoH2v8ijgXQbzn/NOzcJjahRxEuQEmXde25tnNNPJRU/MYr1mfcuQE+HiS/CQ4r+CkJ7TkKgHu6WpTp0bKTA7aRJ1Y3TmgOwf2QjO/wMumR65XOJNQzhFOo6VajpoF/ZPrH+tdx3ZER8P8s1hH8iXxCGIPDy9H9CAPr5Zf96m6ZxrIzZG71OJIhrbD3jWysg1uLDy8SOiOnlxqSrnkjfIdGQH9b1XITWo+ZENPFTACsMtnsWRa2yx+WY93wbbA8VxlDavx3+++pbrdnkTn02tnko76JAnw==}";
    	System.out.println( "send xml--------" + root );
    	handler.execute(root, "/HttpServer/CUP_SVR/2001");
    	//handler.execute(root, "/HttpServer/CUP_SVR/2001");
//    	handler.execute(root, "/JavaFrame-web/HttpServer/CUP_SVR/SQRY00020001");
	}

    /**
     * 执行Http请求
     * 
     * @param body 报文体
     * @param url	http:// + IP + 端口 + action路径
     * @return 
     * @throws HttpException, IOException 
     */
    @SuppressWarnings("deprecation")
	public HttpResponse execute(String body,String url) throws HttpException, IOException {
        HttpClient httpclient = new HttpClient(connectionManager);

		//String ip = "192.168.7.200";
        String ip = "9.1.60.5";
		String port = "12011";
		String urlPre = "http://" + ip + ":" + port + url;
		
		
        // 设置连接超时
		int connectionTimeout = 30000;
        httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);

        // 设置回应超时
        int soTimeout = 60000;
        httpclient.getHttpConnectionManager().getParams().setSoTimeout(soTimeout);

        // 设置等待ConnectionManager释放connection的时间
        httpclient.getParams().setConnectionManagerTimeout(30000);
        System.out.println( urlPre );
        //HttpMethod method = new PostMethod(urlPre+url);
        HttpMethod method = new PostMethod(urlPre);
        method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; text/html; charset=" + DEFAULT_CHARSET);

        // 设置Http Header中的User-Agent属性
        //method.addRequestHeader("User-Agent", "Mozilla/4.0");
        // 设置请求体
        ((PostMethod) method).setRequestBody(body);
        HttpResponse response = new HttpResponse();

        try {
            httpclient.executeMethod(method);
            response.setStringResult(method.getResponseBodyAsString());
            System.out.println( "recv xml-----" );
            System.out.println(method.getResponseBodyAsString());
            response.setResponseHeaders(method.getResponseHeaders());
        } catch (UnknownHostException ex) {
        	ex.printStackTrace();
            return null;
        } catch (IOException ex) {
        	ex.printStackTrace();
            return null;
        } catch (Exception ex) {
        	ex.printStackTrace();
            return null;
        } finally {
            method.releaseConnection();
        }
        return response;
    }
}
