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
package io.jpress.front.controller;

import java.math.BigInteger;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;

import io.jpress.Consts;
import io.jpress.core.cache.ActionCache;
import io.jpress.model.Content;
import io.jpress.model.Taxonomy;
import io.jpress.model.query.ContentQuery;
import io.jpress.model.query.OptionQuery;
import io.jpress.model.query.TaxonomyQuery;
import io.jpress.router.RouterMapping;
import io.jpress.utils.StringUtils;
import io.jpress.wechat.WechatUserInterceptor;

@RouterMapping(url = "/rss")
@Before(WechatUserInterceptor.class)
public class RssController extends Controller {
	private static final String contentType = "text/xml; charset=" + Consts.CHARTSET_UTF8;

	private String title;
	private String description;
	private String link;

	private String webTitle;
	private String webLink;
	private String webLogo;

	private BigInteger taxonomyId;

	@ActionCache
	public void index() {
		doInit();
		doRender();
	}

	private void doInit() {
		String para = getPara();
		if (StringUtils.isNotBlank(para)) {
			taxonomyId = new BigInteger(para);
		}

		webTitle = OptionQuery.me().findValue("web_title");
		if (StringUtils.isBlank(webTitle)) {
			webTitle = OptionQuery.me().findValue("web_name");
		}
		webLink = OptionQuery.me().findValue("web_domain");
		webLogo = OptionQuery.me().findValue("web_logo");
		
		if (webLogo == null)
			webLogo = "";

		if (taxonomyId == null) {
			title = webTitle;
			link = webLink;
			description = OptionQuery.me().findValue("web_subtitle");
		} else {
			Taxonomy taxonomy = TaxonomyQuery.me().findById(taxonomyId);
			if (taxonomy != null) {
				title = taxonomy.getTitle();
				description = taxonomy.getText();
				link = webLink + taxonomy.getUrl();
			}
		}

	}

	private void doRender() {
		StringBuilder xmlBuilder = new StringBuilder();
		buildChannelHeader(xmlBuilder);
		buildChannelInfo(xmlBuilder);

		List<Content> clist = null;
		if (taxonomyId != null) {
			clist = ContentQuery.me().findListInNormal(1, 20, taxonomyId);
		} else {
			clist = ContentQuery.me().findListInNormal(1, 20);
		}

		if (clist != null && !clist.isEmpty()) {
			for (Content content : clist) {
				buildChannelItem(xmlBuilder, content);
			}
		}

		buildChannelFooter(xmlBuilder);
		renderText(xmlBuilder.toString(), contentType);
	}

	private void buildChannelHeader(StringBuilder xmlBuilder) {
		xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xmlBuilder.append("<rss version=\"2.0\">");
		xmlBuilder.append("<channel>");
	}

	private void buildChannelFooter(StringBuilder xmlBuilder) {
		xmlBuilder.append("</channel>");
		xmlBuilder.append("</rss>");
		xmlBuilder.append("<!-- This rss was generated by JPress --> ");
	}

	private void buildChannelInfo(StringBuilder xmlBuilder) {
		xmlBuilder.append("<title>" + title + "</title>");
		xmlBuilder.append("<description>" + description + "</description>");
		xmlBuilder.append("<link>" + link + "</link>");
		xmlBuilder.append("<generator>JPress (http://www.jpress.io) </generator>");
		xmlBuilder.append("<image>");
		xmlBuilder.append("<url>" + webLogo + "</url>");
		xmlBuilder.append("<title>" + webTitle + "</title>");
		xmlBuilder.append("<link>" + webLink + "</link>");
		xmlBuilder.append("</image>");
	}

	private void buildChannelItem(StringBuilder xmlBuilder, Content content) {
		xmlBuilder.append("<item>");
		xmlBuilder.append("<title><![CDATA[ " + content.getTitle() + " ]]></title>");
		xmlBuilder.append("<link>" + webLink + content.getUrl() + "</link>");
		xmlBuilder.append("<description><![CDATA[ " + content.getText() + " ]]></description>");
		xmlBuilder.append("<source>" + webTitle + "</source>");
		xmlBuilder.append("<pubDate>" + content.getModified() + "</pubDate>");
		xmlBuilder.append("</item>");
	}

}
