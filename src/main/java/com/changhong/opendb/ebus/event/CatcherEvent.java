package com.changhong.opendb.ebus.event;

import com.changhong.opendb.ebus.Event;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class CatcherEvent extends Event
{
        public final String message;

        public CatcherEvent(String message)
        {
                super(null);
                this.message = message;
        }
}
