
package com.ibm.cs.utils;

//Misc Imports
import org.apache.log4j.Level;

import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogManager;


/**
 * This class is used to log the messages.
 * 
 * @(#) LoggerUtil.java Created on July 18, 2007 11:30:22 AM Package Declaration: File Name: LoggerUtil.java
 *      Package Name: com.crocs.util Project name: CROCS Type Declaration: Class Name: LoggerUtil Type
 *      Comment:
 * @author GDC
 * @version 1.0
 * @history (C) Copyright 2006-2007 by owner. All Rights Reserved. This software is the confidential
 *          and proprietary information of the owner. ("Confidential Information"). Redistribution
 *          of the source code or binary form is not permitted without prior authorization from the
 *          owner.
 */
public class LoggerUtil {

    YFCLogCategory logger;

    /**
     * Construct a default LoggerUtil
     */
    public LoggerUtil() {
        this.logger = YFCLogCategory.instance("com.custom");
    }

    /**
     * Construct a LoggerUtil with specified name
     * 
     * @param name
     *            LoggerUtil name
     */
    public LoggerUtil(String name) {
        this.logger = YFCLogCategory.instance(name);
    }

    /**
     * Log message
     * 
     * @param level
     *            the level as defined in LogUtil class
     * @param msg
     *            any Object as message.
     */
    public void log(Level level, Object msg) {
        this.logger.log(level, msg);
    }

    /**
     * Error message
     * 
     * @param code
     *            error code
     */
    public void error(String code) {
        if (this.isErrorEnabled()) {
            this.logger.error(ResourceUtil.resolveMsgCode(code));
        }
    }

    /**
     * Error message
     * 
     * @param code
     *            error code
     * @param t
     *            Throwable object
     */
    public void error(String code, Throwable t) {
        if (this.isErrorEnabled()) {
            this.logger.error(ResourceUtil.resolveMsgCode(code), t);
        }
    }

    /**
     * Error message
     * 
     * @param code
     *            error code
     * @param args
     *            Object array
     */
    public void error(String code, Object[] args) {
        if (this.isErrorEnabled()) {
            this.logger.error(ResourceUtil.resolveMsgCode(code, args));
        }
    }

    /**
     * Error message
     * 
     * @param code
     *            error code
     * @param args
     *            Object array
     * @param t
     *            Throwable object
     */
    public void error(String code, Object[] args, Throwable t) {
        if (this.isErrorEnabled()) {
            this.logger.error(ResourceUtil.resolveMsgCode(code, args), t);
        }
    }

    /**
     * Info message
     * 
     * @param code
     *            Info code
     */
    public void info(String code) {
        if (this.isInfoEnabled()) {
            this.logger.info(ResourceUtil.resolveMsgCode(code));
        }
    }

    /**
     * Info message
     * 
     * @param code
     *            Info code
     * @param args
     *            Object array
     */
    public void info(String code, Object[] args) {
        if (this.isInfoEnabled()) {
            this.logger.info(ResourceUtil.resolveMsgCode(code, args));
        }
    }

    /**
     * Log Debug message
     * 
     * @param msg
     *            Message object
     */
    public void debug(Object msg) {
        this.logger.debug(msg);
    }

    /**
     * Log verbose message
     * 
     * @param msg
     *            Message string
     */
    public void verbose(String msg) {
        this.logger.verbose(msg);
    }

    /**
     * Check the enabled Log Level
     * 
     * @return True if Verbose enabled
     */
    public boolean isVerboseEnabled() {
        return YFCLogManager.isLevelEnabled(YFCLogLevel.VERBOSE.toInt());
    }

    /**
     * Check the enabled Log Level
     * 
     * @return True if Debug enabled
     */
    public boolean isDebugEnabled() {
    	// TODO - Uncomment this after debugging.
       // return YFCLogManager.isLevelEnabled(YFCLogLevel.DEBUG.toInt());
    	return true;
    }

    /**
     * Check the enabled Log Level
     * 
     * @return True if Info enabled
     */
    public boolean isInfoEnabled() {
        return YFCLogManager.isLevelEnabled(YFCLogLevel.INFO.toInt());
    }

    /**
     * Check the enabled Log Level
     * 
     * @return True if Error enabled
     */
    public boolean isErrorEnabled() {
        return YFCLogManager.isLevelEnabled(YFCLogLevel.ERROR.toInt());
    }
    
    /**
     * @param methodName
     *            Name of the method to be monitored
     */

    public void beginTimer(String methodName) {
        this.logger.beginTimer(methodName);
    }

    /**
     * @param methodName
     *            Name of the method to be monitored
     */

    public void endTimer(String methodName) {
        this.logger.endTimer(methodName);
    }

    /**
     * Get default package level LoggerUtil.
     * 
     * @return the default LoggerUtil
     */
    public static LoggerUtil getLogger() {
        return new LoggerUtil();
    }

    /**
     * Get LoggerUtil by name.
     * 
     * @param name
     *            Class name
     * @return the LoggerUtil with the name.
     */
    public static LoggerUtil getLogger(String name) {
        return new LoggerUtil(name);
    }

}
