package com.ting.sysadm.config;

import java.util.Date;

import javax.servlet.ServletContext;

import com.blog.action.BlogController;
import com.blog.action.CommentController;
import com.blog.action.VoteController;
import com.blog.model.Blog;
import com.blog.model.Comment;
import com.blog.model.Tags;
import com.blog.model.Visitor;
import com.blog.model.VoteOption;
import com.blog.model.VoteResult;
import com.feel.action.ShareController;
import com.feel.model.Share;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.ViewType;
import com.ting.sysadm.action.CodeController;
import com.ting.sysadm.action.CommonController;
import com.ting.sysadm.action.DownLoadUrl;
import com.ting.sysadm.action.IndexController;
import com.ting.sysadm.action.UserController;
import com.ting.sysadm.interceptor.StaticResourceFilterHandler;
import com.ting.sysadm.model.Fans;
import com.ting.sysadm.model.SysFile;
import com.ting.sysadm.model.User;

/**
 * API引导式配置 系统配置文件
 * 
 * @author aGen
 * 
 */
public class JfinalConfig extends JFinalConfig {
	private boolean isLocal = StringKit.isBlank(System.getenv("MOPAAS_MYSQL1390_HOST"));

	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		// 加载少量必要配置，随后可用getProperty(...)获取值
		loadPropertyFile("dataConfig.txt");
		me.setDevMode(getPropertyToBoolean("devMode", false));
		me.setViewType(ViewType.JSP);
		me.setError404View("/404.html");
		me.setError500View("/500.html");
		// JspRender.setSupportActiveRecord(false);
	}

	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		me.add("/", IndexController.class);
		me.add("/blog", BlogController.class, "/WEB-INF/jsps/blog/");
		me.add("/file", FileUploadController.class, "/WEB-INF/jsps/");
		me.add("/code", CodeController.class, "/WEB-INF/jsps/");
		me.add("/user", UserController.class, "/WEB-INF/jsps/");
		me.add("/comment", CommentController.class, "/WEB-INF/jsps/");
		me.add("/vote", VoteController.class, "/WEB-INF/jsps/");
		me.add("/down",DownLoadUrl.class);
		me.add("/share",ShareController.class,"WEB-INF/jsps/feel/");
		me.add("/common",CommonController.class);
	}

	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		// 自适应配置
		String jdbcUrl, username, password, driver;
		driver = getProperty("driverClass");
		if (isLocal) {
			jdbcUrl = getProperty("jdbcUrl");
			username = getProperty("username");
			password = getProperty("password");
		} else {
			username = System.getenv("MOPAAS_MYSQL1390_USER");
			password = System.getenv("MOPAAS_MYSQL1390_PASSWORD");
			jdbcUrl = "jdbc:mysql://" + System.getenv("MOPAAS_MYSQL1390_HOST") + ":" + System.getenv("MOPAAS_MYSQL1390_PORT") + "/" + System.getenv("MOPAAS_MYSQL1390_NAME");
		}
		DruidPlugin druidPlugin = new DruidPlugin(jdbcUrl, username, password, driver);
		druidPlugin.setInitialSize(3).setMaxActive(10);
		me.add(druidPlugin);
		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
		arp.setShowSql(true);
		arp.setDialect(new MysqlDialect());
		// if (isLocal){
		// arp.setShowSql(true);
		// }
		me.add(arp);
		arp.addMapping("file", "id", SysFile.class);
		arp.addMapping("blog", "id", Blog.class);
		arp.addMapping("user", "id", User.class);
		arp.addMapping("tags", "id", Tags.class);
		arp.addMapping("comment", "id", Comment.class);
		arp.addMapping("visitor", Visitor.class);
		arp.addMapping("voteoption", VoteOption.class);
		arp.addMapping("voteresult", VoteResult.class);
		arp.addMapping("share", Share.class);
		arp.addMapping("fans", Fans.class);
		me.add(new EhCachePlugin());
	}

	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		/**
		 * 获取存储session的拦截器，配置此拦截器在页面才可用${session.attrName}获取session 如果不配置这个全局的拦截器又想要在页面上获取session域的值，则必须在每个view上手动 用annotation方式引入
		 */
		me.add(new SessionInViewInterceptor());
	}

	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		me.add(new StaticResourceFilterHandler());
		//me.add(new CharsetFilterHandler());
	}
	/**
     * 初始化常量
     */
    public void afterJFinalStart(){
       ServletContext servletContext = JFinal.me().getServletContext();
       //设置web 根目录
       servletContext.setAttribute("contextPath", servletContext.getContextPath());
       servletContext.setAttribute("currentDate", new Date());
    }
    public void beforeJFinalStop(){
    	ServletContext servletContext = JFinal.me().getServletContext();
    	servletContext.removeAttribute("contextPath");
    	servletContext.removeAttribute("session");
    }
}
