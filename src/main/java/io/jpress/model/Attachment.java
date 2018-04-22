/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.model;

import io.jpress.model.base.BaseAttachment;
import io.jpress.model.core.Table;
import io.jpress.model.query.UserQuery;
import io.jpress.utils.AttachmentUtils;

/**
 * Generated by JPress.
 */
@Table(tableName = "attachment", primaryKey = "id")
public class Attachment extends BaseAttachment<Attachment> {
	
	private static final long serialVersionUID = 1L;
	
	public final static String STATUS_NORMAL = "normal";//正常	
	public final static String STATUS_DRAFT = "draft";//待审核	
	public final static String STATUS_DELETE = "delete";//审核不通过

	private User user;

	public boolean isImage() {
		return AttachmentUtils.isImage(getPath());
	}

	public User getUser() {
		if (user != null)
			return user;

		if (getUserId() == null)
			return null;

		user = UserQuery.me().findById(getUserId());
		return user;
	}
	
	@Override
	public boolean update() {
		removeCache();
		return super.update();
	}

	@Override
	public boolean delete() {
		removeCache();
		return super.delete();
	}

	@Override
	public boolean save() {
		removeCache();
		return super.save();
	}

	@Override
	public boolean saveOrUpdate() {
		removeCache();
		return super.saveOrUpdate();
	}

	private void removeCache(){
		removeCache(getId());
		removeCache(getId()+getStatus());
	}
	
}
