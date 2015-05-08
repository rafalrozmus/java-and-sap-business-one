/*
 * A simple example on how to unlock a user.
 * For this program to work, you need SAP Business One DI API installed.
 */

/*
 * Import JCO libraries from DI API folder. Two files need to be added to your
 * project libraries - sboapi.jar and sbowrapper.jar
 * The files are usually located in:
 * C:\Program Files (x86)\SAP\SAP Business One DI API\JCO\LIB - 32 bit build
 * C:\Program Files\SAP\SAP Business One DI API\JCO\LIB - 64 bit build
 */
import com.sap.smb.sbo.api.*;

/**
 *
 * @author Rafal.Rozmus
 */
public class UnlockUser {
    
    // declare Company object
    public ICompany company;
    // declare Business Partner object
    public IUsers user;
    // set default value for connection result, 0 will mean success
    private int connectionResult = -1;
    
    public static void main(String[] args) 
    {
        UnlockUser sapConnection = new UnlockUser();
        sapConnection.connect();
        sapConnection.unlockUser();
        // check if connection has been established before disconnecting
        if (sapConnection.getConnectionResult() == 0)
        {
            sapConnection.disconnect();
        }    
    }
    
    /**
     * Add Business Partner
     */
    public void unlockUser() 
    {
        try
        {
            // initialise User object
            user = SBOCOMUtil.newUsers(company);
            // get the user by its InternalKey (INTERNAL_K in OUSR table)
            user.getByKey(40);
            // check if user is locked
            if (user.getLocked() == 1) {
                user.setLocked(0);
                if(user.update() == 0) {
                    System.out.println("Successfully unlocked user: " + user.getUserCode());
                }
                else {
                    // get error message fom SAP Business One Server
                    SBOErrorMessage errMsg = company.getLastError();
                    System.out.println(
                        "Cannot update user: "
                        + errMsg.getErrorMessage()
                        + " "
                        + errMsg.getErrorCode()
                    );
                }
            }
            else {
                System.out.println("User is not locked.");
            }
        }
        catch(SBOCOMException e)
        {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Set all connection parameters, connect to SAP Business One and initialise
     * company instance.
     */
    public void connect() 
    {  
        try 
        {
            // initialise company instance
            company = SBOCOMUtil.newCompany();
            // set database server host
            company.setServer("dbserver");
            // set company database
            company.setCompanyDB("SBODemoGB");
            // set SAP user
            company.setUserName("manager");
            // set SAP user password
            company.setPassword("Password");
            // set SQL server version
            company.setDbServerType(SBOCOMConstants.BoDataServerTypes_dst_MSSQL2012);
            // set whether to use trusted connection to SQL server
            company.setUseTrusted(false);
            // set SAP Business One language
            company.setLanguage(SBOCOMConstants.BoSuppLangs_ln_English);
            // set database user
            company.setDbUserName("sa");
            // set database user password
            company.setDbPassword("SQLPassword");
            // set license server and port
            company.setLicenseServer("licenserver:30000");
            
            // initialise connection
            connectionResult = company.connect();
            
            // if connection successful
            if (connectionResult == 0) 
            {
                System.out.println("Successfully connected to " + company.getCompanyName());
            }
            // if connection failed
            else 
            {
                // get error message fom SAP Business One Server
                SBOErrorMessage errMsg = company.getLastError();
                System.out.println(
                        "Cannot connect to server: "
                        + errMsg.getErrorMessage()
                        + " "
                        + errMsg.getErrorCode()
                );
            }
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            connectionResult = -1;    
        }
    }
    
    /**
     * Disconnect from SAP Business One server.
     */
    public void disconnect() 
    {
        company.disconnect();
        System.out.println("Application disconnected successfully");
    }
    
    /**
     * Get connection result
     * 
     * @return 0 if success, -1 if fail
     */
    public int getConnectionResult()
    {
        return connectionResult;
    }
}
