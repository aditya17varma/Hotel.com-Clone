package servers.jettyServer;

import hotelapp.HotelSearch;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.util.HashMap;

/** This class uses Jetty & servlets to implement server serving hotel and review info */
public class JettyServer {
    private HotelSearch hs;
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
     * Loads hotels, reviews, and creates and inverted index
     */
    public void loadHotelSearch(String hotelPath, String reviewPath, int threads){
        hs = new HotelSearch();
        hs.loadHotels(hotelPath);
        hs.loadReviews(reviewPath, threads);
        hs.createInvertedIndex();
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

        handler.addServlet(HotelServlet.class, "/hotelInfo");
        handler.addServlet(ReviewServlet.class, "/reviews");
        handler.addServlet(IndexServlet.class, "/index");
        handler.addServlet(WeatherServlet.class, "/weather");

        handler.setAttribute("data", hs);
    }

    public void setAttribute(String name, Object value){
        handler.setAttribute(name, value);
    }

//    public static void main(String[] args) throws Exception {
//        if (args.length < 4){
//            System.out.println("Not enough command line arguments provided");
//            System.out.println("Provide in the format:");
//            System.out.println("-reviews <reviewsDirectory> -hotels <hotelsFile> -threads <numThreads> -output <output>");
//            System.exit(1);
//        }
//
//        HashMap<String, String> commands = new HashMap<>();
//
//        for (int i = 0; i < args.length; i = i + 2){
//            String argument = args[i];
//            String value = args[i + 1];
//            commands.put(argument, value);
//        }
//
//        String reviewPath = commands.get("-reviews");
//        String hotelFile = commands.get("-hotels");
//        String threads = commands.get("-threads");
//        int numThreads;
//        if (threads != null){
//            numThreads = Integer.parseInt(commands.get("-threads"));
//        }
//        else {
//            numThreads = 1;
//        }
//        String outputFile = commands.get("-output");
//        if (outputFile == null){
//            outputFile = "output.txt";
//        }
//
//        JettyServer js = new JettyServer();
//        js.loadHotelSearch(hotelFile, reviewPath, numThreads);
//        js.loadServlets();
//        js.start(PORT);
//    }

}
