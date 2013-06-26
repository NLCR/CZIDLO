package cz.nkp.urnnbn.client.forms.processes;

import gwtupload.client.IUploader;
import gwtupload.client.MultiUploader;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader.OnCancelUploaderHandler;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.UploadedInfo;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.forms.Field;

public class UploadFileField extends Field {

	private final Label label;
	private String resultFile = null;
	private MultiUploader uploader = uploader();

	public UploadFileField(String labelContent) {
		label = label(labelContent + ":");
	}

	private Label label(String content) {
		Label result = new Label(content);
		result.setStyleName(css.formLabel());
		return result;
	}

	private MultiUploader uploader() {
		MultiUploader uploader = new MultiUploader();
		uploader.setMaximumFiles(1);

		uploader.addOnCancelUploadHandler(new OnCancelUploaderHandler() {

			@Override
			public void onCancel(IUploader uploader) {
				//System.out.println("canceled " + uploader.fileUrl());
				resultFile = null;
			}
		});

		uploader.addOnFinishUploadHandler(new OnFinishUploaderHandler() {

			// singleUploader.addOnFinishUploadHandler(new
			// OnFinishUploaderHandler() {

			@Override
			public void onFinish(IUploader uploader) {
				if (uploader.getStatus() == Status.SUCCESS) {

					// new PreloadedImage(uploader.fileUrl(), showImage);

					// The server sends useful information to the client by
					// default
					UploadedInfo info = uploader.getServerInfo();
					//System.out.println("File name " + info.name);
					//System.out.println("File content-type " + info.ctype);
					//System.out.println("File size " + info.size);

					// You can send any customized message and parse it
					//System.out.println("Server message: " + info.message);
					resultFile = info.message;
				} else {
					//System.out.println("Uploader status: " + uploader.getStatus());
				}
			}
		});
		return uploader;

	}

	@Override
	public Widget getLabelWidget() {
		return label;
	}

	@Override
	public Widget getContentWidget() {
		return uploader;
	}

	@Override
	public boolean validValueInserted() {
		return resultFile != null;
	}

	@Override
	public String getInsertedValue() {
		return resultFile;
	}

	@Override
	public void disable() {
		// TODO Auto-generated method stub
	}

	@Override
	public void enable() {
		// TODO Auto-generated method stub
	}

}
