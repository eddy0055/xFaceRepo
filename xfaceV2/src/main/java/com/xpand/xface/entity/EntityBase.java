package com.xpand.xface.entity;

import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.StringUtil;

public class EntityBase {
	private String actionCommand;

	public String getActionCommand() {
		if (StringUtil.checkNull(this.actionCommand)) {
			this.actionCommand = ConstUtil.ACTION_COMMAND_ADD;
		}else if (!(ConstUtil.ACTION_COMMAND_ADD.equals(this.actionCommand)||ConstUtil.ACTION_COMMAND_EDIT.equals(this.actionCommand))) {
			this.actionCommand = ConstUtil.ACTION_COMMAND_ADD;
		}
		return actionCommand;
	}

	public void setActionCommand(String actionCommand) {
		this.actionCommand = actionCommand;
	}
}
