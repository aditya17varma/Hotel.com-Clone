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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/** This class uses Jetty & servlets to implement server serving hotel and review info */
public class JettyServer {
    private Object hs;
    private ServletContextHandler servletHandler;
    private ExecutorService executor;
    private Phaser phaser;

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
    }

    /**
     * loadHotelsTable
     * Loads the hotels from the hotel data maps to the database
     */
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

    /**
     * loadReviewsTable
     * Loads the hotel reviews from the hotel data maps to the database
     */
    //todo pool of threads instead of threads[]
    public void loadReviewsTable() {
        executor = Executors.newFixedThreadPool(50);
        phaser = new Phaser();

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        System.out.println("Loading reviews...");
        Map<String, List<Review>> reviewMap = ((HotelSearch)hs).getReviewMap();
        for (String hotelId: reviewMap.keySet()){
            List<Review> tempList = reviewMap.get(hotelId);
            InsertThread worker = new InsertThread(tempList, dbHandler);
            phaser.register();
            executor.submit(worker);
            System.out.println("Thread for :" + hotelId);

        }
        shutdownExecutor();

        System.out.println("Reviews loaded into db!");
    }

    public class InsertThread extends Thread {
        List<Review> threadList;
        DatabaseHandler dbh;

        public InsertThread(List<Review> reviews, DatabaseHandler dbh){
            this.threadList = reviews;
            this.dbh = dbh;
        }

        @Override
        public void run() {
            try{
                for (Review r: threadList){
                    dbh.insertReviews(r);
                }
            }
            finally {
                phaser.arriveAndDeregister();
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
        servletHandler.addServlet(ReviewsServlet.class, "/reviews");
        servletHandler.addServlet(HomepageServlet.class, "/home");

        servletHandler.setAttribute("data", hs);
    }

    /**
     * shutdownExecutor
     * Shuts down the ExecutorService
     */
    public void shutdownExecutor() {
        System.out.println("Executor Shutdown");
        this.executor.shutdown();
        try {
            this.executor.awaitTermination(2, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    public void setAttribute(String name, Object value){
        servletHandler.setAttribute(name, value);
    }

}
