/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.mobile.screens.assetlist.interactor;

import android.util.Pair;

import com.liferay.mobile.android.service.BatchSessionImpl;
import com.liferay.mobile.android.service.JSONObjectWrapper;
import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.v62.assetentry.AssetEntryService;
import com.liferay.mobile.screens.assetlist.AssetEntry;
import com.liferay.mobile.screens.base.list.BaseListCallback;
import com.liferay.mobile.screens.base.list.BaseListInteractor;
import com.liferay.mobile.screens.service.MobilewidgetsassetentryService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * @author Silvio Santos
 */
public class AssetListInteractorImpl
	extends BaseListInteractor<AssetEntry, AssetListRowsListener> implements AssetListInteractor {


    public AssetListInteractorImpl(int targetScreenletId) {
		super(targetScreenletId);
	}

    public void loadRows(
			long groupId, long classNameId, int startRow, int endRow, Locale locale)
		throws Exception {
        this._groupId = groupId;
        this._classNameId = classNameId;
        loadRows(startRow, endRow, locale);
	}

    @Override
    protected BaseListCallback<AssetEntry> getCallback(Pair<Integer, Integer> rowsRange) {
        return new AssetListCallback(getTargetScreenletId(), rowsRange);
    }

    @Override
    protected void sendPageRequests(BatchSessionImpl batchSession, int startRow, int endRow, Locale locale) throws Exception {
        sendGetPageRowsRequest(
                batchSession, _groupId, _classNameId, startRow, endRow, locale);

        sendGetEntriesCountRequest(batchSession, _groupId, _classNameId);
    }

	protected void sendGetEntriesCountRequest(
			Session session, long groupId, long classNameId)
		throws Exception {

		JSONObject entryQueryAttributes = configureEntryQueryAttributes(
			groupId, classNameId);

		JSONObjectWrapper entryQuery = new JSONObjectWrapper(
			entryQueryAttributes);

		AssetEntryService service = new AssetEntryService(session);
		service.getEntriesCount(entryQuery);
	}

	protected void sendGetPageRowsRequest(
			Session session, long groupId, long classNameId, int startRow, int endRow,
			Locale locale)
		throws Exception {

		JSONObject entryQueryAttributes = configureEntryQueryAttributes(
			groupId, classNameId);
		entryQueryAttributes.put("start", startRow);
		entryQueryAttributes.put("end", endRow);

		JSONObjectWrapper entryQuery = new JSONObjectWrapper(
			entryQueryAttributes);

		MobilewidgetsassetentryService service =
			new MobilewidgetsassetentryService(session);
		service.getAssetEntries(entryQuery, locale.toString());
	}

    protected JSONObject configureEntryQueryAttributes(
            long groupId, long classNameId)
            throws JSONException {

        JSONObject entryQueryAttributes = new JSONObject();
        entryQueryAttributes.put("classNameIds", classNameId);
        entryQueryAttributes.put("groupIds", groupId);
        entryQueryAttributes.put("visible", "true");

        return entryQueryAttributes;
    }

    @Override
	protected void validate(
		int startRow, int endRow, Locale locale) {

		if (_groupId <= 0) {
			throw new IllegalArgumentException(
				"GroupId cannot be 0 or negative");
		}

		if (_classNameId <= 0) {
			throw new IllegalArgumentException(
				"ClassNameId cannot be 0 or negative");
		}

		super.validate(startRow,endRow,locale);
	}

    private long _groupId;
    private long _classNameId;

}