/*
 * A simple example on how to establish connection to SAP Business One
 */

import com.sap.smb.sbo.api.*;

/**
 *
 * @author Rafal Rozmus (rozmus.rafal@gmail.com)
 */
public class ConnectToServer 
{
    
    public ICompany company;
    
    public static void main(String[] args) 
    {
        ConnectToServer server = new ConnectToServer();
        server.connect();
    }
    
    /**
     * Set all connection parameters, connect to SAP Business One, initialise
     * company instance and disconnect from server.
     * 
     * @return 0 if success, -1 if fail
     */
    public int connect() 
    {  
        int connectionResult = 0;
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
                this.disconnect();
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
            return -1;    
        }
        return connectionResult;
    }
    
    /**
     * Disconnect from SAP Business One server.
     */
    public void disconnect() 
    {
        company.disconnect();
        System.out.println("Application disconnected successfully");
    }
}
