package com.SEM.InvestmentHoustSystem;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.NumberFormatter;

import com.SEM.InvestmentHoustSystem.Request.RequestType;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;


/**
 * Created by roie on 27/05/2017.
 */
public class MainWindow extends JFrame {
	private static final long serialVersionUID = -6255882573476988587L;
	private JPanel topPanel = new JPanel(new BorderLayout(20, 0));
    private JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
    private JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
    private JLabel logo = new JLabel(new ImageIcon("100PlusLogo.png"));
    private Color hunPlusBlue = new Color(46, 53, 137);

    /**
     * Menu Components
     */
    private JPanel menuPanel = new JPanel(new GridLayout(1, 4, 13, 13));
    private MenuLabel accountLabel = new MenuLabel("My Account");
    private MenuLabel portfolioLabel = new MenuLabel("Portfolio");
    private MenuLabel requestsLabel = new MenuLabel("Bid/Ask Requests");
    private MenuLabel stocksLabel = new MenuLabel("Stock Market");

    private AccountPanel accountPanel;
    private PortfolioPanel portfolioPanel;
    private RequestsPanel requestsPanel;
    private StocksPanel stocksPanel;

    private final String BID ="Bid",ASK="Ask";
    private CardLayout cl = new CardLayout();
    private LoginWindow lw;
    private final double COMMISSION = 0.0035;

    private Account account;
    private static String currentPanel = "My Account";

    //private InvestmentHouseFacade facade;
    private InvestmentHouseFacad facad;
    private Timer timer;

    public MainWindow(InvestmentHouseFacad ihf,Account a, LoginWindow loginWindow) throws IOException, ClassNotFoundException {
        this.facad = ihf;
      //  ClientServerRequest csr = facad.login(email,password);
        account = a;
        System.out.println(account);
        System.out.println(account.getName()+account.getEmail()+account.getBalance());
        accountPanel = new AccountPanel();
        ArrayList<StockDetails> investorStocks = new ArrayList<>(facad.getPortfolioByInvoker(account).getInvestorStocks());


        stocksPanel = new StocksPanel(BID);
        portfolioPanel = new PortfolioPanel(investorStocks);
        requestsPanel = new RequestsPanel();

        lw = loginWindow;

        updatesOnTime();
        topPanelSetup();
        centerPanelSetup();
        bottomPanelSetup();

        setup();
    }

    private void setup() {
        final int WIDTH = 1200, HEIGHT = 700;
        setLayout(new BorderLayout());
        add(centerPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        topPanel.add(menuPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                facad.logOut();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                facad.logOut();
            }
        });
        setSize(new Dimension(WIDTH, HEIGHT));
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setVisible(true);
    }

    private void updatesOnTime(){
        final int DELAY = 2000;
        Thread timeUpdate = new Thread(new Runnable() {
            @Override
            public void run() {
                timer = new Timer(DELAY, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        stocksPanel.updateStocks();

                        requestsPanel.updateRequests();
                        requestsPanel.repaint();

                        portfolioPanel.updateMyStocks();
                        portfolioPanel.repaint();

                        accountPanel.updateAccount();
                    }
                });
                timer.start();
            }
        });
       timeUpdate.start();
    }
    private void topPanelSetup() {

        topPanel.add(logo, BorderLayout.CENTER);
        topPanel.setBackground(Color.WHITE);

    }

    private void centerPanelSetup() {
        centerPanel.setLayout(cl);
        centerPanel.setBackground(hunPlusBlue);


        accountLabel.addMouseListener(new LabelClicked());
        portfolioLabel.addMouseListener(new LabelClicked());
        requestsLabel.addMouseListener(new LabelClicked());
        stocksLabel.addMouseListener(new LabelClicked());

        menuPanel.setBorder(new EmptyBorder(13, 13, 13, 13));
        menuPanel.setBackground(hunPlusBlue);
        menuPanel.add(accountLabel);
        menuPanel.add(portfolioLabel);
        menuPanel.add(requestsLabel);
        menuPanel.add(stocksLabel);


        centerPanel.add(accountPanel, "My Account");
        centerPanel.add(portfolioPanel, "Portfolio");
        JScrollPane  requestsPanelJScrollPane = new JScrollPane(requestsPanel);
        centerPanel.add(requestsPanelJScrollPane, "Bid/Ask Requests");
        JScrollPane stocksJScrollPane = new JScrollPane(stocksPanel);
        centerPanel.add(stocksJScrollPane, "Stock Market");
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        cl.show(centerPanel, "My Account");
        accountLabel.setBackground(hunPlusBlue);
        accountLabel.setForeground(Color.white);
    }

    private void bottomPanelSetup() {
        bottomPanel.setBackground(new Color(233, 235, 235));
        bottomPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        MenuLabel logout = new MenuLabel("Log-out");
        logout.setBackground(hunPlusBlue);
        logout.setForeground(Color.white);
        logout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(timer != null) timer.stop();
                MainWindow.this.setVisible(false);
                lw.setVisible(true);
            }
        });
        bottomPanel.add(logout, BorderLayout.EAST);
    }

    private class MenuLabel extends JLabel {
		private static final long serialVersionUID = 230841446286864549L;
		EmptyBorder emptyBorder;
        public MenuLabel(String label) {
            super(label);
            setFont(new Font("Chalkboard SE", Font.BOLD, 22));
            emptyBorder = new EmptyBorder(17, 17, 17, 17);
            setBorder(emptyBorder);
            setBackground(Color.WHITE);
            setForeground(hunPlusBlue);
            setOpaque(true);

        }
    }

    private class InvestmentHousePanel extends JPanel {
		private static final long serialVersionUID = 728755677951786550L;

		public InvestmentHousePanel() {
            setBackground(hunPlusBlue);
            setForeground(Color.white);
        }

    }

    private class AccountPanel extends InvestmentHousePanel {
		private static final long serialVersionUID = 6021614959053962669L;
		private String labelNames[] = new String[]{"Name:","Balance","Email:","Status:"};
        private JLabel balanceField,nameField,emailField, typeField;
        public AccountPanel() {
            JLabel jlbs[] = createHeaders(labelNames);
            setBorder(new EmptyBorder(40, 50, 40, 150));
            add(jlbs[0]);
            nameField =new JLabel(account.getName());
            setUpField(nameField);

            add(nameField);
            add(jlbs[1]);

            balanceField =new JLabel(String.format("$%,.2f", account.getBalance().floatValue()));
            setUpField(balanceField);
            if(account.getBalance()>0){
                balanceField.setForeground(Color.GREEN);
            }else if(account.getBalance()<0){
                balanceField.setForeground(Color.RED);
            }
            add(balanceField);

            add(jlbs[2]);
            emailField = new JLabel(account.getEmail());
            setUpField(emailField);
            add(emailField);

            add(jlbs[3]);
            typeField =new JLabel(account.getType().name());
            setUpField(typeField);
            add(typeField);
        }
        public AccountPanel(String[] labelNames,String[] values) {
            JLabel labels[] = createHeaders(labelNames);
            for(int i=0; i < labelNames.length;i++){
                add(labels[i]);
                JLabel jl  =new JLabel(values[i]);
                if(labels[i].getText().equals("Total Gain/Loss:")){
                    if(jl.getText().charAt(0) == '-')
                        jl.setForeground(Color.RED);
                    else
                        jl.setForeground(Color.GREEN);
                }else {
                    jl.setForeground(Color.WHITE);
                }
                jl.setFont(new Font(null,Font.PLAIN,20));
                add(jl);
            }
        }
        private void setUpField(JLabel label){
            label.setFont(new Font(null, Font.PLAIN, 20));
            label.setForeground(Color.WHITE);
        }
        private JLabel[] createHeaders(String[] headers){
            JLabel jlbs[] = new JLabel[headers.length];
            setLayout(new GridLayout(headers.length, 2, 3, 0));

            for (int i = 0; i < jlbs.length; i++) {
                jlbs[i] = new JLabel(headers[i]);
                jlbs[i].setFont(new Font(null, Font.BOLD, 20));
                jlbs[i].setForeground(Color.white);
            }
            return jlbs;
        }
        
        public void updateAccount() {
        	account = facad.getAccountByEmail(account.getEmail());
            balanceField.setText(String.format("$%,.2f",account.getBalance()));
            AccountPanel.this.repaint();
        }
    }

    private class PortfolioPanel extends InvestmentHousePanel {
		private static final long serialVersionUID = -7978276976472162097L;
		private JPanel topPanel;
        private StocksPanel myStocksPanel;
        private ArrayList<StockDetails> investorStocks;
        private PortfolioPanel(ArrayList<StockDetails> investorStocks){
            setLayout(new GridLayout(2,1));
            setBackground(hunPlusBlue);
            this.investorStocks = investorStocks;
           // trimInvestorStocks();

            topPanelSetup();
            myStocksPanelSetup();
            add(topPanel);
            JScrollPane jsp = new JScrollPane(myStocksPanel);
            add(jsp);

        }

        private synchronized void trimInvestorStocks(){
            Iterator<StockDetails> iterator = investorStocks.iterator();
            investorStocks.removeIf(stockDetails -> iterator.next().getAmount()<1 );
        }

        private void topPanelSetup(){
            String fields[] = new String[]{"Number Of Stocks:","Total Value:","Total Expenses:","Total Gain/Loss:","Stocks:"};
            double totalValue = sumTotalValue(),totalExpense = sumTotalExpenses(),totalGainLoss = totalValue - totalExpense;
            String values[] = new String[]{investorStocks.size()+"",String.format("$%,.2f",totalValue), String.format("$%,.2f",totalExpense),String.format("$%,.2f",totalGainLoss),""};
            topPanel = new AccountPanel(fields,values);
        }
        private double sumTotalValue(){
            double sum=0;
            Map<String, AnalyzedStock> stocks = new HashMap<>();
            facad.getAllStocks().forEach(s -> stocks.put(s.getId(), s));
            for(StockDetails stock : investorStocks){
                sum += stock.getAmount() * stocks.get(stock.getStockId()).getQuote();
            }
            return sum;
        }

        private double sumTotalExpenses(){
            double sum=0;
            for(StockDetails stock : investorStocks){
                sum += stock.getAmount() * stock.getPurchasePrice();
            }
            return sum;
        }
        private void myStocksPanelSetup(){
            myStocksPanel = new StocksPanel(investorStocks,ASK, new String[]{"Name","Symbol","Closing Price","Turn Over","Change","Bid Price","Shares"," "});
        }
        public void updateMyStocks(){
            investorStocks = facad.getPortfolioByInvoker(account).getInvestorStocks();

            trimInvestorStocks();
            double totalValue = sumTotalValue(),totalExpense = sumTotalExpenses(),totalGainLoss = totalValue - totalExpense;
            if(investorStocks != null) {
                ((JLabel) topPanel.getComponents()[1]).setText(investorStocks.size() + "");
                ((JLabel) topPanel.getComponents()[3]).setText(String.format("$%,.2f",totalValue));
                ((JLabel) topPanel.getComponents()[5]).setText(String.format("$%,.2f",totalExpense));
                ((JLabel) topPanel.getComponents()[7]).setText(String.format("$%,.2f",totalGainLoss));
                ((JLabel) topPanel.getComponents()[7]).setForeground(totalGainLoss<0?Color.RED:Color.GREEN);
            }
            myStocksPanel.updateInvestorStocks(investorStocks);

            repaint();
        }
        public ArrayList<StockDetails> getInvestorStocks() {
            return investorStocks;
        }

    }

    private class RequestsPanel extends StocksPanel {
		private static final long serialVersionUID = -6265668418406355729L;
		private final int MAX_NUM_OF_REQUEST = 20;
        private String headers[]= new String[]{"Stock Name","Symbol","Type","Max Price","Min Price","Shares","Status"};
        private StockFieldLabel labels[] = new StockFieldLabel[headers.length*(MAX_NUM_OF_REQUEST+1)];
        private ArrayList<Request> requests = new ArrayList<>();

        public RequestsPanel(){
            setLayout(new GridLayout(MAX_NUM_OF_REQUEST+1,headers.length,7,7));
            setBorder(new EmptyBorder(5,5,5,5));
            initiateLabels();
            updateRequests();
        }

        private void initiateLabels(){
            int i;
            for(i=0;i<headers.length;i++){
                labels[i] = new StockFieldLabel(headers[i],type.HEADER);
                add(labels[i]);
            }
            for(i =headers.length ; i<labels.length;i++){
                labels[i] = new StockFieldLabel("   ",type.ELSE);
                add(labels[i]);
            }
        }

        private void updateRequests() {
            requests = facad.getAccountsRequests(account);
            if(requests.size()==0)
                return;

            Map<String, AnalyzedStock> stocks = new HashMap<>();
            facad.getAllStocksOffline().forEach(s -> stocks.put(s.getId(), s));
            
            for(int i =0;i<headers.length;i++){
                labels[i].setText(headers[i]);
            }

            for (int i = headers.length,k=0; i <= headers.length * requests.size() ; i+= headers.length,k++) {
                Request r = requests.get(k);
                
                String requestFields[] = new String[]{stocks.get(r.getStockId()).getName(), r.getStockId(), r.getType().toString(), String.format("%,.2f", r.getMaxPrice()),
                        String.format("%,.2f", r.getMinPrice()), r.getAmount() + "", r.getStatus().toString()};
                for (int j = i; j < i + headers.length; j++) {
                    labels[j].setText(requestFields[j - i]);
                    if (j == i + headers.length - 1) {
                        switch (r.getStatus()) {
                        	case CANCELED: {
                                labels[j].setForeground(Color.RED);
                                break;
                            }
                            case PENDING: {
                                labels[j].setForeground(Color.YELLOW);
                                break;
                            }
                            case COMPLETED: {
                                labels[j].setForeground(Color.GREEN);
                                break;
                            }
                        }
                    }

                }
            }
           // RequestsPanel.this.repaint();
        }

        public ArrayList<Request> getRequests() {
            return requests;
        }

    }

    enum type {HEADER, TURNOVER, ELSE}

    private class StocksPanel extends InvestmentHousePanel {
		private static final long serialVersionUID = 5981325579507621683L;
		private String headers[] = new String[]{"Name","Symbol","Closing Price","Turn Over","Change",""};
        private String investorHeaders[];
        private ArrayList<StockFieldLabel> labels= new ArrayList<>();
        private ArrayList<AnalyzedStock> stocks;
        int helpers=3;

        /** NEW ONE FOR STOCK MARKET*/
        public StocksPanel(String action){
            stocks =facad.getAllStocks();
            setLayout(new GridLayout(stocks.size()+1, headers.length));
            insertHeaders(headers);
            insertStocks(stocks,BID);
        }

        public StocksPanel(){
        }

        private void updateStocks(){
            StocksPanel.this.removeAll();
            stocks.removeAll(stocks);
            stocks.addAll(facad.getAllStocks());

            setLayout(new GridLayout(stocks.size() + 1, headers.length));
            setBorder(new EmptyBorder(3, 20, 20, 20));
            insertHeaders(headers);
            insertStocks(stocks,BID);
            cl.show(centerPanel,"My Account");
            cl.show(centerPanel,currentPanel);
            repaint();
        }

        /**InvestorStocks*/
        public StocksPanel(ArrayList<StockDetails> investorStocks, String action, String[] headers1) {
            setLayout(new GridLayout(investorStocks.size()+1+helpers, headers1.length,0,0));
            setBorder(new EmptyBorder(5, 0, 5, 0));
            investorHeaders=headers1;
            insertHeaders(headers1);
            insertMyStocks(investorStocks,action);
        }


        private void updateInvestorStocks(ArrayList<StockDetails> investorStocks){
            StocksPanel.this.removeAll();

            setLayout(new GridLayout(investorStocks.size() + 1 + helpers, headers.length,0,0));
            setBorder(new EmptyBorder(5, 3, 5, 0));
            insertHeaders(investorHeaders);
            insertMyStocks(investorStocks,ASK);
        }
        private void insertHeaders(String[] headers){
            for(String header:headers){
                add(new StockFieldLabel(header,type.HEADER));
            }
        }
        private void insertStocks(ArrayList<AnalyzedStock> allAnalyzedStocks, String action){
            for (AnalyzedStock analyzedStock : allAnalyzedStocks) {
                insertStock(analyzedStock);
                StockFieldLabel button = new StockFieldLabel(analyzedStock,action);
                labels.add(button);
                add(button);
            }
        }
        private void insertMyStocks(ArrayList<StockDetails> investorStocks, String action){
            for (StockDetails stock : investorStocks) {
            	AnalyzedStock as = facad.StockDetailsToAanalyzedStock(stock);
            	if(stock.getAmount()>0) {
                    insertStock(as);
                    add(new StockFieldLabel(String.format("%,.2f",stock.getPurchasePrice()), type.ELSE));
                    add(new StockFieldLabel(stock.getAmount() + "", type.ELSE));
                    add(new StockFieldLabel(as, action));
                }
            }

            for(int i=0 ;i<investorHeaders.length*helpers;i++){
                add(new StockFieldLabel("",type.ELSE));
            }
        }
        
        private void insertStock(AnalyzedStock analyzedStock){
            StockFieldLabel arr[] =new StockFieldLabel[]{new StockFieldLabel(analyzedStock.getName().length()>15?analyzedStock.getName().substring(0,15):analyzedStock.getName()
                    , type.ELSE),
            new StockFieldLabel(analyzedStock.getId(), type.ELSE),
            new StockFieldLabel(String.format("%,.2f", analyzedStock.getQuote()), type.ELSE),
            new StockFieldLabel(String.format("%,.2f%%",analyzedStock.getTurnOver()), type.TURNOVER),
            new StockFieldLabel(String.format("%,.2f", analyzedStock.getChange()), type.ELSE)};
            for(StockFieldLabel l : arr){
                labels.add(l);
                add(l);
            }
        }
    }

    public class StockFieldLabel extends JLabel {
		private static final long serialVersionUID = -448269176468785160L;

		public StockFieldLabel(String s, type t) {
            super(s);
            setBackground(hunPlusBlue);
            setOpaque(true);
            switch (t) {
                case HEADER: {
                    setFont(new Font("", Font.BOLD, 16));
                    setForeground(Color.WHITE);
                    break;
                }
                case TURNOVER: {
                    setFont(new Font("", Font.PLAIN, 16));
                    if (s.charAt(0) == '-') {
                        setForeground(Color.RED);
                    } else {
                        StringBuilder sb = new StringBuilder("+");
                        sb.append(s);
                        setText(sb.toString());
                        setForeground(Color.GREEN);
                    }
                    break;
                }
                case ELSE: {
                    setFont(new Font("", Font.PLAIN, 16));
                    setForeground(Color.WHITE);
                    break;
                }
            }
        }

        public StockFieldLabel(AnalyzedStock analyzedStock, String action) {
            super("     "+action);
            setFont(new Font("Chalkboard SE", Font.BOLD, 22));
            setBackground(Color.WHITE);
            setForeground(hunPlusBlue);
            setBorder(new LineBorder(hunPlusBlue, 7));
            setOpaque(true);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    new RequestWindow(action, analyzedStock);
                }
            });
        }
    }

    private class RequestWindow extends JFrame {
		private static final long serialVersionUID = -3922314858966986590L;
		private JFormattedTextField maxPriceField, minPriceField, amountField;
        private JPanel topPanel = new JPanel(new BorderLayout(20, 0));
        private JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        private JPanel bottomPanel = new JPanel(new BorderLayout(0, 0));
        private JLabel actionButton;
        private StockFieldLabel weightedPrices = new StockFieldLabel(String.format("%,.2f - %,.2f $", 0.0f, 0.0f), type.ELSE);

        public RequestWindow(String action, AnalyzedStock analyzedStock) {
            super(action);
            setLayout(new BorderLayout());
            centerPanel.setLayout(new GridLayout(9, 2));
            centerPanel.setBorder(new LineBorder(hunPlusBlue, 4));

            centerPanel.add(new StockFieldLabel("Name:", type.HEADER));
            centerPanel.add(new StockFieldLabel(analyzedStock.getName(), type.ELSE));
            centerPanel.add(new StockFieldLabel("Symbol:", type.HEADER));
            centerPanel.add(new StockFieldLabel(analyzedStock.getId(), type.ELSE));
            centerPanel.add(new StockFieldLabel("Closing Price:", type.HEADER));
            centerPanel.add(new StockFieldLabel(String.format("$%,.2f", analyzedStock.getQuote()), type.ELSE));
            centerPanel.add(new StockFieldLabel("Turn Over:", type.HEADER));
            centerPanel.add(new StockFieldLabel(String.format("%,.2f%%",analyzedStock.getTurnOver()), type.TURNOVER));
            centerPanel.add(new StockFieldLabel("Change:", type.HEADER));
            centerPanel.add(new StockFieldLabel(String.format("%,.2f", analyzedStock.getChange()), type.ELSE));
            centerPanel.add(new StockFieldLabel("Max Price:", type.HEADER));

            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
            DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
            decimalFormat.setGroupingUsed(true);//CHANGE IF HAVING PROBLEMS
            maxPriceField = new JFormattedTextField(decimalFormat);
            maxPriceField.addKeyListener(new PricesListener());
            centerPanel.add(maxPriceField);

            centerPanel.add(new StockFieldLabel("Min Price:", type.HEADER));
            minPriceField = new JFormattedTextField(decimalFormat);
            minPriceField.addKeyListener(new PricesListener());
            centerPanel.add(minPriceField);
            centerPanel.add(new StockFieldLabel("Shares:", type.HEADER));

            NumberFormat amountFormat = NumberFormat.getIntegerInstance();
            amountFormat.setGroupingUsed(false);
            NumberFormatter amountFormatter = new NumberFormatter(amountFormat);
            amountFormatter.setValueClass(Integer.class);
            amountFormatter.setAllowsInvalid(false);
            amountFormatter.setMinimum(0);
            amountFormatter.setMaximum(Integer.MAX_VALUE);
            amountField = new JFormattedTextField(amountFormatter);
            amountField.addKeyListener(new PricesListener());
            Thread sumChecker = new Thread(new Runnable() {
                Timer t = new Timer(400, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        checkIfPriceHasChanged();
                        if (!RequestWindow.this.isVisible()) {
                            t.stop();
                        }
                    }
                });

                @Override
                public void run() {
                    t.start();
                }
            });
            sumChecker.start();
            centerPanel.add(amountField);

            centerPanel.add(new StockFieldLabel("With Commission:", type.HEADER));
            centerPanel.add(weightedPrices);

            actionButton = new JLabel("     " + action);
            actionButton.setBackground(Color.WHITE);
            actionButton.setForeground(hunPlusBlue);
            actionButton.setOpaque(true);
            actionButton.setFont(new Font("Chalkboard SE", Font.BOLD, 26));

            actionButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    //todo maybe in the future check that fields are filled
                    double minPrice = Double.parseDouble(minPriceField.getText().replace(",",""));
                    double maxPrice = Double.parseDouble(maxPriceField.getText().replace(",",""));
                    int amount = Integer.parseInt(amountField.getText().replace(",",""));
                    RequestType rt = action.equalsIgnoreCase("bid")? RequestType.BID : RequestType.ASK;

                    /**
                     * 0) min is bigger than max
                     * 1) Bidding more than the money the investor has
                     * 2) Asking more shares than the investor has
                     * */
                    boolean errorOcc = false;
                    StringBuilder errors = new StringBuilder("");
                    if(maxPrice < minPrice){
                        errors.append("Maximum Price Should Be Bigger Than Minimum Price\n");
                        errorOcc = true;
                    }
                    int sumOfMaximumPricesRequests=0;

                    for(Request r:requestsPanel.getRequests())
                        sumOfMaximumPricesRequests +=r.getType().equals(RequestType.BID)&&r.getStatus().equals(Request.RequestStatus.PENDING)?r.getMaxPrice()*r.getAmount():0;

                    if(maxPrice*amount*(1+COMMISSION)>  facad.getAccountByEmail(account.getEmail()).getBalance()- sumOfMaximumPricesRequests && rt.equals(RequestType.BID)){
                        errors.append("Not Enough Balance\n");
                        errorOcc = true;
                    }

                    int shares=0;
                    ArrayList<StockDetails> inveStocks = portfolioPanel.getInvestorStocks();
                    for(int i=0; i < inveStocks.size(); i++){
                        if(inveStocks.get(i).getStockId().equals(analyzedStock.getId())) {
                            shares = inveStocks.get(i).getAmount();
                            break;
                        }
                    }

                    if(rt.equals(RequestType.ASK)&& amount > shares){
                        errors.append("You Can't Sell More Shares Than You Have");
                        errorOcc = true;
                    }
                    if(!errorOcc) {
                        Request request = new Request(rt, account.getEmail(), analyzedStock.getId(), minPrice, maxPrice, amount, new Date());
                        RequestWindow.this.setVisible(false);
                        facad.sendRequest(request);
                        request.setStatus(Request.RequestStatus.PENDING);
                        requestsPanel.updateRequests();
                        Thread sendMail = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                MessgingSystem.send(account.getEmail(),"Your "+rt.name()+" Request Was Submitted Successfully",
                                        "Your request with the following details:"
                                                +"\nType: "+request.getType()
                                                +"\nStock: "+analyzedStock.getName()
                                                +"\nSymbol: "+analyzedStock.getId()
                                                +"\nAmount Of Shares: "+request.getAmount()
                                                +"\nMax Price: "+request.getMaxPrice()
                                                +"\nMin Price: "+request.getMinPrice()
                                                +"\nTimestamp: "+request.getTimeStamp()
                                                +"\n\n\nBest Regards,\n100Plus Team");
                            }
                        });
                        sendMail.start();

                    }else{
                        MainWindow.this.setAlwaysOnTop(false);
                        RequestWindow.this.setAlwaysOnTop(false);
                        JOptionPane.showMessageDialog(null,errors,"Error",JOptionPane.ERROR_MESSAGE);
                        RequestWindow.this.setAlwaysOnTop(true);
                      //  MainWindow.this.setAlwaysOnTop(true);
                    }

                }
            });
            bottomPanelSetup();
            setup();
        }

        private void setup() {
            final int WIDTH = 400, HEIGHT = 500;
            topPanel.setBackground(Color.WHITE);
            centerPanel.setBackground(hunPlusBlue);
            bottomPanel.setBackground(hunPlusBlue);
            topPanel.add(logo, BorderLayout.CENTER);

            add(centerPanel, BorderLayout.CENTER);
            add(topPanel, BorderLayout.NORTH);
            add(bottomPanel, BorderLayout.SOUTH);

            setSize(WIDTH, HEIGHT);
            setLocationRelativeTo(null);
            setAlwaysOnTop(true);
            setResizable(false);
            setVisible(true);
        }

        private void bottomPanelSetup(){
            bottomPanel.setBorder(new EmptyBorder(30, 5, 30, 5));
            bottomPanel.add(actionButton, BorderLayout.CENTER);
            bottomPanel.add(new JLabel("                               "), BorderLayout.EAST);
            bottomPanel.add(new JLabel("                               "), BorderLayout.WEST);
        }

        private Double[] getPricesWithCommissions() {
            Integer amount = Integer.parseInt(amountField.getText().replace(",", ""));
            int commissionSign = actionButton.getText().contains("Bid")? 1: -1;
            double commission = Double.parseDouble(maxPriceField.getText().replace(",", "")) * COMMISSION * amount*commissionSign;
            double maxPayment = Double.parseDouble(maxPriceField.getText().replace(",", "")) * amount + commission;
            double minPayment = maxPriceField.getText().isEmpty() ? commission : Double.parseDouble(minPriceField.getText().replace(",", "")) * amount + commission;

            return new Double[]{minPayment, maxPayment, commission};
        }

        private class PricesListener extends KeyAdapter {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyChar() == KeyEvent.VK_BACK_SPACE &&((JTextField)e.getSource()).getText().length() == 1){
                    ((JTextField)e.getSource()).setText("0");
                }
                if ((Character.isDigit(e.getKeyChar()) || e.getKeyChar() == KeyEvent.VK_BACK_SPACE)) {
                    checkIfPriceHasChanged();
                }
            }
        }

        private void checkIfPriceHasChanged() {
            if (!amountField.getText().isEmpty()
                    && !maxPriceField.getText().isEmpty() && !minPriceField.getText().isEmpty()) {
                Double prices[] = getPricesWithCommissions();
                weightedPrices.setText(String.format("%,.2f - %,.2f $", prices[0].floatValue(), prices[1].floatValue()));
            }
        }
    }
        private class LabelClicked extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                cl.show(centerPanel, ((MenuLabel) e.getSource()).getText());
                currentPanel =((MenuLabel) e.getSource()).getText();
                ((MenuLabel) e.getSource()).setForeground(Color.white);
                ((MenuLabel) e.getSource()).setBackground(hunPlusBlue);
                for (Component component : menuPanel.getComponents()) {
                    if (component !=  e.getSource()) {
                        component.setBackground(Color.WHITE);
                        component.setForeground(hunPlusBlue);
                    }
                }
            }
        }

}

