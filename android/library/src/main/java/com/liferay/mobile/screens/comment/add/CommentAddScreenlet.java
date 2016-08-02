package com.liferay.mobile.screens.comment.add;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import com.liferay.mobile.screens.R;
import com.liferay.mobile.screens.base.BaseScreenlet;
import com.liferay.mobile.screens.base.interactor.Interactor;
import com.liferay.mobile.screens.comment.add.interactor.CommentAddInteractor;
import com.liferay.mobile.screens.comment.add.interactor.CommentAddInteractorImpl;
import com.liferay.mobile.screens.comment.add.view.CommentAddViewModel;
import com.liferay.mobile.screens.context.LiferayServerContext;
import com.liferay.mobile.screens.models.CommentEntry;

/**
 * @author Alejandro Hernández
 */
public class CommentAddScreenlet extends BaseScreenlet<CommentAddViewModel, Interactor>
	implements CommentAddListener {

	public CommentAddScreenlet(Context context) {
		super(context);
	}

	public CommentAddScreenlet(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override protected View createScreenletView(Context context, AttributeSet attributes) {
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(
			attributes, R.styleable.CommentAddScreenlet, 0, 0);

		_className = typedArray.getString(R.styleable.CommentAddScreenlet_className);

		_classPK = castToLong(typedArray.getString(R.styleable.CommentAddScreenlet_classPK));

		_groupId = castToLongOrUseDefault(typedArray.getString(
			R.styleable.CommentAddScreenlet_groupId), LiferayServerContext.getGroupId());

		int layoutId = typedArray.getResourceId(
			R.styleable.CommentAddScreenlet_layoutId, getDefaultLayoutId());

		typedArray.recycle();

		return LayoutInflater.from(context).inflate(layoutId, null);
	}

	@Override protected Interactor createInteractor(String actionName) {
		return new CommentAddInteractorImpl(getScreenletId());
	}

	@Override
	protected void onUserAction(String userActionName, Interactor interactor, Object... args) {
		try {
			((CommentAddInteractor) interactor).addComment(
				_groupId, _className, _classPK, getViewModel().getBody());
		} catch (Exception e) {
			onAddCommentFailure(getViewModel().getBody(), e);
		}
	}

	@Override public void onAddCommentFailure(String body, Exception e) {
		if (getListener() != null) {
			getListener().onAddCommentFailure(body, e);
		}
	}

	@Override public void onAddCommentSuccess(CommentEntry commentEntry) {
		if (getListener() != null) {
			getListener().onAddCommentSuccess(commentEntry);
		}
	}

	public CommentAddListener getListener() {
		return _listener;
	}

	public void setListener(CommentAddListener listener) {
		_listener = listener;
	}

	public String getClassName() {
		return _className;
	}

	public void setClassName(String className) {
		this._className = className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public void setClassPK(long classPK) {
		this._classPK = classPK;
	}

	public long getGroupId() {
		return _groupId;
	}

	public void setGroupId(long groupId) {
		this._groupId = groupId;
	}

	private CommentAddListener _listener;
	private long _groupId;
	private String _className;
	private long _classPK;
}
