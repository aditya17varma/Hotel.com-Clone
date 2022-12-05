package hotelapp;

import jettyServer.*;
import org.apache.velocity.app.VelocityEngine;

public class HotelServer {
	public static final int PORT = 8080;

	public static void main(String[] args) throws Exception {
		JettyServer js = new JettyServer();
		js.loadHotelSearch("input/hotels/hotels.json", "input/reviews", 3);
		js.loadServlets();
//		js.loadHotelsTable();
		js.loadReviewsTable();

		VelocityEngine velocity = new VelocityEngine();
		velocity.init();

		js.setAttribute("templateEngine", velocity);

		js.start(PORT);
	}
}