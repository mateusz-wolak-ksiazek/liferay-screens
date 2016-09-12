/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.mobile.screens.userportrait;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import com.liferay.mobile.screens.R;
import com.liferay.mobile.screens.base.BaseScreenlet;
import com.liferay.mobile.screens.base.MediaStoreRequestShadowActivity;
import com.liferay.mobile.screens.base.interactor.Interactor;
import com.liferay.mobile.screens.context.LiferayScreensContext;
import com.liferay.mobile.screens.context.SessionContext;
import com.liferay.mobile.screens.userportrait.interactor.UserPortraitInteractorListener;
import com.liferay.mobile.screens.userportrait.interactor.load.UserPortraitLoadInteractorImpl;
import com.liferay.mobile.screens.userportrait.interactor.upload.UserPortraitUploadEvent;
import com.liferay.mobile.screens.userportrait.interactor.upload.UserPortraitUploadInteractorImpl;
import com.liferay.mobile.screens.userportrait.view.UserPortraitViewModel;
import com.liferay.mobile.screens.util.LiferayLogger;

/**
 * @author Javier Gamarra
 * @author Jose Manuel Navarro
 */
public class UserPortraitScreenlet extends BaseScreenlet<UserPortraitViewModel, Interactor>
	implements UserPortraitInteractorListener {

	public static final String UPLOAD_PORTRAIT = "UPLOAD_PORTRAIT";
	public static final String LOAD_PORTRAIT = "LOAD_PORTRAIT";
	private static final String STATE_SUPER = "userportrait-super";
	private static final String STATE_FILE_PATH = "userportrait-filePath";
	private String filePath;
	private boolean autoLoad;
	private boolean male;
	private long portraitId;
	private String uuid;
	private long userid;
	private boolean editable;
	private UserPortraitListener listener;

	public UserPortraitScreenlet(Context context) {
		super(context);
	}

	public UserPortraitScreenlet(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public UserPortraitScreenlet(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public UserPortraitScreenlet(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public void load() {
		performUserAction(LOAD_PORTRAIT);
	}

	public void openCamera() {
		startShadowActivityForMediaStore(MediaStoreRequestShadowActivity.TAKE_PICTURE_WITH_CAMERA);
	}

	public void openGallery() {
		startShadowActivityForMediaStore(MediaStoreRequestShadowActivity.SELECT_IMAGE_FROM_GALLERY);
	}

	@Override
	public Bitmap onEndUserPortraitLoadRequest(Bitmap bitmap) {
		Bitmap finalImage = bitmap;

		if (listener != null) {
			finalImage = listener.onUserPortraitLoadReceived(bitmap);

			if (finalImage == null) {
				finalImage = bitmap;
			}
		}

		getViewModel().showFinishOperation(finalImage);

		return finalImage;
	}

	@Override
	public void onUserPortraitUploaded(Long uuid) {
		if (listener != null) {
			listener.onUserPortraitUploaded();
		}

		getViewModel().showFinishOperation(UPLOAD_PORTRAIT);

		((UserPortraitLoadInteractorImpl) getInteractor(LOAD_PORTRAIT)).start(uuid);
	}

	@Override
	public void onPicturePathReceived(String picturePath) {
		performUserAction(UPLOAD_PORTRAIT, picturePath);
	}

	@Override
	public void error(Exception e, String userAction) {
		if (listener != null) {
			listener.error(e, userAction);
		}

		getViewModel().showFailedOperation(userAction, e);
	}

	public void setListener(UserPortraitListener listener) {
		this.listener = listener;
	}

	public boolean isMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	public long getPortraitId() {
		return portraitId;
	}

	public void setPortraitId(long portraitId) {
		this.portraitId = portraitId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public long getUserId() {
		return userid;
	}

	public void setUserId(long userId) {
		userid = userId;
	}

	public boolean getEditable() {
		return editable;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isAutoLoad() {
		return autoLoad;
	}

	public void setAutoLoad(boolean autoLoad) {
		this.autoLoad = autoLoad;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	protected void autoLoad() {
		if (((portraitId != 0) && (uuid != null)) || (userid != 0)) {
			try {
				load();
			} catch (Exception e) {
				LiferayLogger.e("Error loading portrait", e);
			}
		}
	}

	@Override
	protected View createScreenletView(Context context, AttributeSet attributes) {
		TypedArray typedArray =
			context.getTheme().obtainStyledAttributes(attributes, R.styleable.UserPortraitScreenlet, 0, 0);

		autoLoad = typedArray.getBoolean(R.styleable.UserPortraitScreenlet_autoLoad, true);
		male = typedArray.getBoolean(R.styleable.UserPortraitScreenlet_male, true);
		portraitId = typedArray.getInt(R.styleable.UserPortraitScreenlet_portraitId, 0);
		uuid = typedArray.getString(R.styleable.UserPortraitScreenlet_uuid);
		editable = typedArray.getBoolean(R.styleable.UserPortraitScreenlet_editable, false);

		userid = castToLongOrUseDefault(typedArray.getString(R.styleable.UserPortraitScreenlet_userId), 0L);

		if (SessionContext.hasUserInfo() && portraitId == 0 && uuid == null && userid == 0) {
			userid = SessionContext.getCurrentUser().getId();
		}

		int layoutId = typedArray.getResourceId(R.styleable.UserPortraitScreenlet_layoutId, getDefaultLayoutId());

		typedArray.recycle();

		return LayoutInflater.from(context).inflate(layoutId, null);
	}

	@Override
	protected Interactor createInteractor(String actionName) {
		if (UPLOAD_PORTRAIT.equals(actionName)) {
			return new UserPortraitUploadInteractorImpl();
		} else {
			return new UserPortraitLoadInteractorImpl();
		}
	}

	@Override
	protected void onUserAction(String userActionName, Interactor interactor, Object... args) {
		if (UPLOAD_PORTRAIT.equals(userActionName)) {
			UserPortraitUploadInteractorImpl userPortraitInteractor =
				(UserPortraitUploadInteractorImpl) getInteractor(userActionName);
			String path = (String) args[0];
			if (userid != 0) {
				userPortraitInteractor.start(new UserPortraitUploadEvent(path));
			}
		} else {
			UserPortraitLoadInteractorImpl userPortraitLoadInteractor =
				(UserPortraitLoadInteractorImpl) getInteractor(userActionName);
			if (portraitId != 0 && uuid != null) {
				userPortraitLoadInteractor.start(male, portraitId, uuid);
			} else {
				if (SessionContext.hasUserInfo() && userid == 0) {
					userPortraitLoadInteractor.start(SessionContext.getCurrentUser().getId());
				} else {
					userPortraitLoadInteractor.start(userid);
				}
			}
		}
	}

	@Override
	protected void onScreenletAttached() {
		if (autoLoad) {
			autoLoad();
		}
	}

	@Override
	protected void onRestoreInstanceState(Parcelable inState) {
		Bundle bundle = (Bundle) inState;
		super.onRestoreInstanceState(bundle.getParcelable(STATE_SUPER));
		filePath = bundle.getString(STATE_FILE_PATH);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(STATE_SUPER, super.onSaveInstanceState());
		bundle.putString(STATE_FILE_PATH, filePath);
		return bundle;
	}

	private void startShadowActivityForMediaStore(int mediaStore) {
		//We need to force the creation of the interactor to get the event from the shadow activity
		getInteractor(UPLOAD_PORTRAIT);

		Activity activity = LiferayScreensContext.getActivityFromContext(getContext());

		Intent intent = new Intent(activity, MediaStoreRequestShadowActivity.class);
		intent.putExtra(MediaStoreRequestShadowActivity.MEDIA_STORE_TYPE, mediaStore);
		intent.putExtra(MediaStoreRequestShadowActivity.SCREENLET_ID, getScreenletId());

		activity.startActivity(intent);
	}
}