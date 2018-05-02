package com.sys.commons.ueditor.upload;

import javax.servlet.http.HttpServletRequest;

import com.sys.commons.ueditor.ActionConfig;
import com.sys.commons.ueditor.define.State;
import com.sys.commons.ueditor.manager.IUeditorFileManager;

public class Uploader {
	private HttpServletRequest request = null;
	private ActionConfig conf = null;

	public Uploader(HttpServletRequest request, ActionConfig conf) {
		this.request = request;
		this.conf = conf;
	}

	public final State doExec(IUeditorFileManager fileManager) {
		String filedName = conf.getFieldName();
		State state = null;
		if (conf.isBase64()) {
			state = Base64Uploader.save(fileManager, request.getParameter(filedName), conf);
		} else {
			state = BinaryUploader.save(fileManager, request, conf);
		}
		return state;
	}
}
