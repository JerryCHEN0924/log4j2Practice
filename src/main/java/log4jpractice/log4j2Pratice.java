package log4jpractice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class log4j2Pratice  {
	// 定義日誌紀錄器物件
	public static final Logger LOGGER = LogManager.getLogger(log4j2Pratice.class);

	public static void main(String[] args) {
		/*
		 * 日誌訊息輸出
		 * 一般只會用到四種，嚴重層級從最低層級依序往上:debug → info → warn → error 
		 */
		LOGGER.fatal("致命的訊息");
		LOGGER.error("錯誤訊息");
		LOGGER.warn("警告訊息");
		LOGGER.info("一般資訊");
		LOGGER.debug("debug");
		LOGGER.trace("trace追蹤");
	}

}
