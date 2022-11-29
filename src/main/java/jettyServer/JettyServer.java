package jettyServer;

import hotelapp.HotelSearch;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

/** This class uses Jetty & servlets to implement server serving hotel and review info */
public class JettyServer {
    private Object hs;
    private ServletContextHandler handler;

    /**
     * Function that starts the server
     * @throws Exception throws exception if access failed
     */
    public void start(int PORT) throws Exception {
        Server server = new Server(PORT);

        server.setHandler(handler);

        server.start();
        server.join();
    }

    /**
     * loadHotelSearch
     * Initializes and loads the hotelSearch instance variable
     * Loads hotels, reviews, and creates inverted index and hotelKeywordMap
     */
    public void loadHotelSearch(String hotelPath, String reviewPath, int threads){
        hs = new HotelSearch();
        ((HotelSearch)hs).loadHotels(hotelPath);
        ((HotelSearch)hs).loadReviews(reviewPath, threads);
        ((HotelSearch)hs).createInvertedIndex();
        ((HotelSearch)hs).createHotelKeywordMap();
    }

    /**
     * loadServlets
     * Initializes the handler instance variable
     * Loads handlers:
     * HotelServlet
     * ReviewServlet
     * IndexServlet
     * WeatherServlet
     */
    public void loadServlets(){
        handler = new ServletContextHandler(ServletContextHandler.SESSIONS);

        handler.addServlet(RegistrationServlet.class, "/register");
        handler.addServlet(LoginServlet.class, "/login");
        handler.addServlet(KeywordSearchServlet.class, "/search");
        handler.addServlet(HotelInfoReviewServlet.class, "/hotelInfoReview");
        handler.addServlet(AddReviewServlet.class, "/addReview");
        handler.addServlet(LogoutServlet.class, "/logout");
        handler.addServlet(ModifyReviewServlet.class, "/modifyReview");
        handler.addServlet(EditReviewServlet.class, "/editReview");
        handler.addServlet(DeleteReviewServlet.class, "/deleteReview");

        handler.setAttribute("data", hs);
    }

    public void setAttribute(String name, Object value){
        handler.setAttribute(name, value);
    }

}
