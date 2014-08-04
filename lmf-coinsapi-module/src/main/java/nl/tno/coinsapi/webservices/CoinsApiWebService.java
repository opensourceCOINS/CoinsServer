package nl.tno.coinsapi.webservices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

@ApplicationScoped
@Path("/" + CoinsApiWebService.PATH)
public class CoinsApiWebService {

    public static final String PATH = "coinsapi";
 
    /**
     * Get the version of the COINS API
     * @return The actual version of the API
     */
    @Path("/version")
    @GET
    @Produces("application/json")
    public String getVersion() {
    	return "0.1 premature";
    }

    /**
     * Test method to be deleted
     * @param parameter
     * @param request
     * @return
     */
    @Path("/aap")
    @GET
    @Produces("application/json")
    public String aap(@QueryParam("parameter") String parameter, @Context HttpServletRequest request) {
    	if (request==null) {
    		return "no request";
    	}
    	return parameter;
    }
    
    /**
     * Upload a COINS file into the Marmotta database
     * @param pFile
     * @param request
     * @return OK if the uploading was successful
     */
    @POST
    @Path("/upload")
    public String upload(@HeaderParam("file") File pFile, @Context HttpServletRequest request) {
    	StringBuilder sb = new StringBuilder();
    	try {
			InputStream stream = request.getInputStream();
			if (stream==null) {
				sb.append("null");
			}
			else {
				sb.append("try to unzip it...");
				ZipInputStream zipStream = new ZipInputStream(stream);
				ZipEntry entry = zipStream.getNextEntry();
				while (entry!=null) {
					sb.append(entry.getName());
					sb.append("<BR/>");
					entry = zipStream.getNextEntry();
				}
				zipStream.closeEntry();
				zipStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
		return sb.toString();
    }
    
    public static void main(String... args) {
    	try {
			ZipInputStream zipStream = new ZipInputStream(new FileInputStream("E:/coins/Tutorial4/D.ccr"));
			ZipEntry entry = zipStream.getNextEntry();
			while (entry!=null) {
				System.err.println(entry.getName());
				entry = zipStream.getNextEntry();
			}
			zipStream.closeEntry();
			zipStream.close();
           
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
}
