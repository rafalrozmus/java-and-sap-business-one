/*
 * A simple example on how to create a new Business Partner record together with
 * Contact Pesrons information and add it to SAP Business One.
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
public class AddBusinessPartnerAndContactEmployee {
    
    // declare Company object
    public ICompany company;
    // declare Business Partner object
    public IBusinessPartners bp;
    // declare Contact Employee object
    public IContactEmployees contactEmployee; 
    // set default value for connection result, 0 will mean success
    private int connectionResult = -1;
    
    public static void main(String[] args) 
    {
        AddBusinessPartnerAndContactEmployee sapConnection = new AddBusinessPartnerAndContactEmployee();
        sapConnection.connect();
        sapConnection.addBusinessPartner();
        // check if connection has been established before disconnecting
        if (sapConnection.getConnectionResult() == 0)
        {
            sapConnection.disconnect();
        }    
    }
    
    /**
     * Add Business Partner
     */
    public void addBusinessPartner() 
    {
        try
        {
            // initialise Business Partner object
            bp = SBOCOMUtil.newBusinessPartners(company);
            // set BP's account code
            bp.setCardCode("ZZZ002");
            // set BP's name
            bp.setCardName("ZZZ2 Test Business Partner");
            // set BP type (0=Customer, 1=Supplier, 2=Lead)
            bp.setCardType(0);
            // decide whether it's a company or private person (0=Company, 1=Private)
            bp.setCompanyPrivate(0);
            // Add first Contact Employee
            bp.getContactEmployees().add();
            bp.getContactEmployees().setCurrentLine(0);
            bp.getContactEmployees().setName("John Smith");
            bp.getContactEmployees().setFirstName("John");
            bp.getContactEmployees().setLastName("Smith");
            bp.getContactEmployees().setE_Mail("John.Smith@example.com");
            bp.getContactEmployees().setMobilePhone("07777777777");
            
            // Add second Contact Employee
            bp.getContactEmployees().setCurrentLine(1);
            bp.getContactEmployees().setName("Laura Smith");
            bp.getContactEmployees().setFirstName("Laura");
            bp.getContactEmployees().setLastName("Smith");
            bp.getContactEmployees().setE_Mail("Laura.Smith@example.com");
            bp.getContactEmployees().setMobilePhone("07777777777");
            
            // Another way of adding Contact Employee
            // Use ContactEmployees object instead of BP object
            contactEmployee = bp.getContactEmployees();
            contactEmployee.add();
            contactEmployee.setCurrentLine(2);
            contactEmployee.setName("John Doe");
            contactEmployee.setFirstName("John");
            contactEmployee.setLastName("Doe");
            contactEmployee.setE_Mail("John.Doe@example.com");
            // add Business Partner
            if (bp.add() == 0) 
            {
                System.out.println("Successfully added Business Partner");
            }
            else 
            {
                // get error message fom SAP Business One Server
                SBOErrorMessage errMsg = company.getLastError();
                System.out.println(
                        "Cannot add Business Partner: "
                        + errMsg.getErrorMessage()
                        + " "
                        + errMsg.getErrorCode()
                );
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
