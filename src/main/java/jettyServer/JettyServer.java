package jettyServer;

import hotelapp.Hotel;
import hotelapp.HotelSearch;
import hotelapp.Review;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.util.List;
import java.util.Map;

/** This class uses Jetty & servlets to implement server serving hotel and review info */
public class JettyServer {
    private Object hs;
    private ServletContextHandler servletHandler;

    /**
     * Function that starts the server
     * @throws Exception throws exception if access failed
     */
    public void start(int PORT) throws Exception {
        Server server = new Server(PORT);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase("templates");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] {resourceHandler, servletHandler});

        server.setHandler(handlers);

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

    public void loadHotelsTable(){
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        System.out.println("Loading hotels in to db...");
        Map<String, Hotel> hotelMap = ((HotelSearch)hs).getHotelMap();
        for (String hotelId: hotelMap.keySet()){
            Hotel tempHotel = hotelMap.get(hotelId);
            dbHandler.insertHotel(tempHotel);
        }
        System.out.println("Hotels loaded into db!");
    }

    //todo pool of threads instead of threads[]
    public void loadReviewsTable() {
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        System.out.println("Loading reviews...");
        Map<String, List<Review>> reviewMap = ((HotelSearch)hs).getReviewMap();
        InsertThread[] threads = new InsertThread[reviewMap.keySet().size()];
        int i = 0;
        for (String hotelId: reviewMap.keySet()){
            List<Review> tempList = reviewMap.get(hotelId);
//            for (Review r: tempList){
//                dbHandler.insertReviews(r);
//            }
            threads[i] = new InsertThread(tempList, dbHandler);
            System.out.println("Thread for :" + hotelId);
            threads[i].start();
            i++;
        }

        try{
            for (int j = 0; j < threads.length; j++){
                threads[j].join();
            }
        }
        catch (InterruptedException e){
            System.out.println(e);
        }
        System.out.println("Reviews loaded into db!");
    }

    public static class InsertThread extends Thread {
        List<Review> threadList;
        DatabaseHandler dbh;

        public InsertThread(List<Review> reviews, DatabaseHandler dbh){
            this.threadList = reviews;
            this.dbh = dbh;
        }

        @Override
        public void run() {
            for (Review r: threadList){
                dbh.insertReviews(r);
            }
        }
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
        servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);

        servletHandler.addServlet(RegistrationServlet.class, "/register");
        servletHandler.addServlet(LoginServlet.class, "/login");
        servletHandler.addServlet(KeywordSearchServlet.class, "/search");
        servletHandler.addServlet(HotelInfoReviewServlet.class, "/hotelInfoReview");
        servletHandler.addServlet(AddReviewServlet.class, "/addReview");
        servletHandler.addServlet(LogoutServlet.class, "/logout");
        servletHandler.addServlet(ModifyReviewServlet.class, "/modifyReview");
        servletHandler.addServlet(EditReviewServlet.class, "/editReview");
        servletHandler.addServlet(DeleteReviewServlet.class, "/deleteReview");
//        handler.addServlet(RegistrationServlet.class, "/reservation");
        servletHandler.addServlet(ExpediaLinksServlet.class, "/expedia");
        servletHandler.addServlet(ClearExpediaServlet.class, "/clearExpedia");
        servletHandler.addServlet(UpdateExpediaServlet.class, "/updateExpedia");
        servletHandler.addServlet(WeatherServlet.class, "/weather");

        servletHandler.setAttribute("data", hs);
    }

    public void setAttribute(String name, Object value){
        servletHandler.setAttribute(name, value);
    }

}
