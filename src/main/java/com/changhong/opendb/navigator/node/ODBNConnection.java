package com.changhong.opendb.navigator.node;

import com.changhong.opendb.model.ConnectionModel;

/**
 * @author Luo Tiansheng
 * @since 2026/3/25
 */
public class ODBNConnection extends ODBNode
{
        private final ConnectionModel model;

        public ODBNConnection(ConnectionModel model)
        {
                super(model.getName());
                this.model = model;
        }

        @Override
        public String getName()
        {
                return model.getName();
        }
}
