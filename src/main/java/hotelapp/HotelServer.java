package hotelapp;

import jettyServer.*;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class HotelServer {
	public static final int PORT = 8080;
	private HotelSearch hs;
	private ServletContextHandler handler;

//	/**
//	 * Function that starts the server
//	 * @throws Exception throws exception if access failed
//	 */
//	public void start() throws Exception {
//		Server server = new Server(PORT);
//
//		server.setHandler(handler);
//
//		server.start();
//		server.join();
//	}
//
//	/**
//	 * loadHotelSearch
//	 * Initializes and loads the hotelSearch instance variable
//	 * Loads hotels, reviews, and creates and inverted index
//	 */
//	public void loadHotelSearch(String hotelPath, String reviewPath, int threads){
//		hs = new HotelSearch();
//		hs.loadHotels(hotelPath);
//		hs.loadReviews(reviewPath, threads);
//		hs.createInvertedIndex();
//	}
//
//	/**
//	 * loadServlets
//	 * Initializes the handler instance variable
//	 * Loads handlers:
//	 * HotelServlet
//	 * ReviewServlet
//	 * IndexServlet
//	 * WeatherServlet
//	 */
//	public void loadServlets(){
//		handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
//
//		handler.addServlet(HotelServlet.class, "/hotelInfo");
//		handler.addServlet(ReviewServlet.class, "/reviews");
//		handler.addServlet(IndexServlet.class, "/index");
//		handler.addServlet(WeatherServlet.class, "/weather");
//
//		handler.setAttribute("data", hs);
//	}

	public static void main(String[] args) throws Exception {
		JettyServer js = new JettyServer();
		js.loadHotelSearch("input/hotels/hotels.json", "input/reviews", 3);
		js.loadServlets();

		VelocityEngine velocity = new VelocityEngine();
		velocity.init();

		js.setAttribute("templateEngine", velocity);

		js.start(PORT);

	}




}