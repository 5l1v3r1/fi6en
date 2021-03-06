package org.red5.fi6en.roomservice;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.red5.fi6en.core.Application;
import org.red5.fi6en.util.HibernateUtil;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class RoomServiceServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
	private static Logger log = Red5LoggerFactory.getLogger(Application.class,"fi6en");
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html;charset=UTF-8");
		
		StringBuilder sb = new StringBuilder();
		
		//Select * from files
		Session session = sessionFactory.openSession();
		//String sql = "select * from rooms where is_open = 1";
		String sql = "select * from rooms";
		
		SQLQuery query = session.createSQLQuery(sql);
        query.addEntity("rooms", Room.class);
        List results = query.list();
        Iterator iter = results.iterator();
		
		sb.append("<rooms>");
		while (iter.hasNext()) {
			Room u = (Room) iter.next();
			//closed room not listed...
			if (u.isIs_open() == false && u.isIs_public() == false) continue;
			//public, private and waited rooms list..
			else {
				String icon = "";
				if (u.isIs_public())
					icon = "room_public.png";
				if (!u.isIs_public())
					icon = "room_private.png";
				if (!u.isIs_open())
					icon = "room_closed.png";
				
				sb.append("<room>");
				sb.append("<id>" + u.getId() + "</id>");
				sb.append("<name>" + u.getName() + "</name>");
				//planned room date => waited to private
				if (icon == "room_closed.png") {
					Long timeRoom = u.getStarttime().getTime();
					Long timeCurrent = System.currentTimeMillis();
					if (timeRoom < timeCurrent) {
						icon = "room_private.png";
						sb.append("<ispublic>" + "false" + "</ispublic>");
						sb.append("<isopen>" + "false" + "</isopen>");
					}
					else {
						icon = "room_closed.png";
						sb.append("<ispublic>" + "false" + "</ispublic>");
						sb.append("<isopen>" + u.isIs_open().toString() + "</isopen>");
					}
				}
				else {
					sb.append("<ispublic>" + u.isIs_public().toString() + "</ispublic>");
					sb.append("<isopen>" + u.isIs_open().toString() + "</isopen>");
				}
				
				icon = "resources/icons/" + icon;
				sb.append("<icon>" + icon + "</icon>");
				sb.append("<comment>" + u.getComment() + "</comment>");
				sb.append("</room>");
			}
		}
		sb.append("</rooms>");

		PrintWriter out = response.getWriter();
		out.print(sb.toString());

		

	}

}