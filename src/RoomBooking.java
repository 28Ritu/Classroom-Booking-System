/**
 * @author ritu
 **/
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.io.FileReader;
import javax.naming.Binding;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * The Class InReader.
 */
class InReader {
    static BufferedReader reader;
    static StringTokenizer tokenizer;
    
    /** call this method to initialize reader for InputStream */
    static void init(InputStream input) {
        reader = new BufferedReader(
                     new InputStreamReader(input) );
        tokenizer = new StringTokenizer("");
    }

    /** get next word */
    static String next() throws IOException {
        while ( ! tokenizer.hasMoreTokens() ) {
            //TODO add check for eof if necessary
            tokenizer = new StringTokenizer(
                   reader.readLine() );
        }
        return tokenizer.nextToken();
    }
    
    static String nextLine() throws IOException
    {
    	return reader.readLine();
    }
    
    static int nextInt() throws IOException {
        return Integer.parseInt( next() );
    }
    
    static double nextDouble() throws IOException {
        return Double.parseDouble( next() );
    }
    
    static float nextFloat() throws IOException {
    	return Float.parseFloat( next() );
    }
}

/**
 * The Class RoomBooking.
 */
public class RoomBooking extends Application implements Serializable
{
	/** The room. */
	private Room room;
	
	/** The rooms. */
	private List<Room> rooms, room_req;
	
	/** The admin list. */
	private List<Admin> admin_list;
	
	/** The faculty list. */
	private List<Faculty> faculty_list;
	
	/** The student list. */
	private List<Student> student_list;
	
	/** The course data. */
	private CourseDatabase course_data;
	
	/**
	 * Instantiates a new room booking.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	public RoomBooking() throws IOException, ClassNotFoundException
	{
		rooms=new ArrayList<Room>();
		room_req=new ArrayList<Room>();
		retrieve_roominfo();
		File f1=new File("./src/AdminList");
		File f2=new File("./src/FacultyList");
		File f3=new File("./src/StudentList");
		File f4=new File("./src/RoomBookedList");
		File f5=new File("./src/RoomRequestList");
		if (!f1.exists() && !f2.exists() && !f3.exists() && !f4.exists() && !f5.exists())
		{
			admin_list=new ArrayList<Admin>();
			faculty_list=new ArrayList<Faculty>();
			student_list=new ArrayList<Student>();
			serialize_Adminlist(admin_list);
			serialize_Facultylist(faculty_list);
			serialize_Studentlist(student_list);
			serialize_RoomBookedList(rooms);
			serialize_RoomReq(room_req);	
		}
		else
		{
			admin_list=deserialize_adminlist();
			faculty_list=deserialize_facultylist();
			student_list=deserialize_studentlist();
			rooms=deserialize_RoomBookedList();
			room_req=deserialize_RoomReq();
		}
		course_data=new CourseDatabase();	
		course_data.retrieve_course(rooms);
	}
	
	/**
	 * Retrieve roominfo.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void retrieve_roominfo() throws IOException 
	{
		BufferedReader infile=new BufferedReader(new FileReader("./src/AP_Project.csv"));
		infile.readLine();
		String nextline=infile.readLine();
		nextline=infile.readLine();
		while (nextline!=null)
		{
			String []fields=nextline.split(",");
			String course_name=fields[1].trim();
			int day_count=0, i;
			for (i=6; i<fields.length-2; i++)
			{
				day_count+=1;
				if ((fields[i].equals(" -")==false) && (fields[i].equals(" -\"")==false))
				{
				    room=new Room();
					room.setcourse(course_name);
					room.setday(day_count);
					room.setaudience("CSE-2");
					String []time_venue=fields[i].split("\\$");
					String []time=time_venue[0].split("-");
					String room_time="";
					if (time[0].contains("AM"))
					{
						time[0]=time[0].replace(":", "");
						room_time+=time[0].substring(0, time[0].length()-2).trim();
					}
					else if (time[0].contains("PM"))
					{
						time[0]=time[0].replace(":", "");
						int time1=Integer.parseInt(time[0].substring(0, time[0].length()-2).trim())+1200;
						room_time+=time1;
					}
					room_time+="-";
					if (time[1].contains("AM"))
					{
						time[1]=time[1].replace(":", "");
						room_time+=time[1].substring(0, time[1].length()-2).trim();
					}
					else if (time[1].contains("PM"))
					{
						time[1]=time[1].replace(":", "");
						int time1=Integer.parseInt(time[1].substring(0, time[1].length()-2).trim())+1200;
						room_time+=time1;
					}
					room.settime(room_time.trim());
					room.setroom(time_venue[1].trim());

					switch(time_venue[1].trim())
					{
					case "C01":
					case "C11": 
					case "C21": room.setcapacity(300);
						break;
					case "C02":
					case "C03":
					case "C04":
					case "C12":
					case "C13":
					case "C14":
					case "C22":
					case "C23":
					case "C24": room.setcapacity(60);
						break;
					case "LR1":
					case "LR2":
					case "LR3":
					case "S01":
					case "S02":
					case "L21":
					case "L22":
					case "L23":
					case "L33":
					case "SCL": room.setcapacity(30);
						break;
					}
					rooms.add(room);
				}
			}
			for (; i<fields.length; i++)
			{
				if ((fields[i].equals(" -")==false) && (fields[i].equals(" -\"")==false))
				{
					String []Tut=fields[i].split(";");
					for (int j=0; j<Tut.length; j++)
					{
						String []day_time_venue=Tut[j].split("\\$");
						String []Tut_rooms=day_time_venue[2].split("/");
						for (int k=0; k<Tut_rooms.length; k++)
						{
							room=new Room();
							room.setcourse(course_name);
							room.setaudience("CSE-2");
							room.setday(Integer.parseInt(day_time_venue[0].trim()));
							String []time=day_time_venue[1].trim().split("-");
							String room_time="";
							if (time[0].contains("AM"))
							{
								time[0]=time[0].replace(":", "");
								room_time+=time[0].substring(0, time[0].length()-2).trim();
							}
							else if (time[0].contains("PM"))
							{
								time[0]=time[0].replace(":", "");
								int time1=Integer.parseInt(time[0].substring(0, time[0].length()-2).trim())+1200;
								room_time+=time1;
							}
							room_time+="-";
							if (time[1].contains("AM"))
							{
								time[1]=time[1].replace(":", "");
								room_time+=time[1].substring(0, time[1].length()-2).trim();
							}
							else if (time[1].contains("PM"))
							{
								time[1]=time[1].replace(":", "");
								int time1=Integer.parseInt(time[1].substring(0, time[1].length()-2).trim())+1200;
								room_time+=time1;
							}
							room.settime(room_time.trim());
							Tut_rooms[k]=Tut_rooms[k].replace('"', ' ');
							room.setroom(Tut_rooms[k].trim());
							switch(Tut_rooms[k].trim())
							{
							case "C01":
							case "C11": 
							case "C21": room.setcapacity(300);
								break;
							case "C02":
							case "C03":
							case "C04":
							case "C12":
							case "C13":
							case "C14":
							case "C22":
							case "C23":
							case "C24": room.setcapacity(60);
								break;
							case "LR1":
							case "LR2":
							case "LR3":
							case "S01":
							case "S02":
							case "L21":
							case "L22":
							case "L23":
							case "L33":
							case "SCL": room.setcapacity(30);
								break;
							}
							//room.display();
							rooms.add(room);
						}
					}
				}
			}
			nextline=infile.readLine();
		}
		infile.close();
	}
	
	/**
	 * Serialize adminlist.
	 *
	 * @param admin_list the admin list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void serialize_Adminlist(List<Admin> admin_list) throws IOException
	{
        ObjectOutputStream outFile=null;
        try
        {
        	outFile=new ObjectOutputStream(new FileOutputStream("./src/AdminList"));
        	outFile.writeObject(admin_list);
        }
        finally
        {
        	outFile.close();
        }
	}
	
	/**
	 * Serialize facultylist.
	 *
	 * @param faculty_list the faculty list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void serialize_Facultylist(List<Faculty> faculty_list) throws IOException
	{
        ObjectOutputStream outFile=null;
        try
        {
        	outFile=new ObjectOutputStream(new FileOutputStream("./src/FacultyList"));
        	outFile.writeObject(faculty_list);
        }
        finally
        {
        	outFile.close();
        }
	}
	
	/**
	 * Serialize studentlist.
	 *
	 * @param student_list the student list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void serialize_Studentlist(List<Student> student_list) throws IOException
	{
        ObjectOutputStream outFile=null;
        try
        {
        	outFile=new ObjectOutputStream(new FileOutputStream("./src/StudentList"));
        	outFile.writeObject(student_list);
        }
        finally
        {
        	outFile.close();
        }
	}
	
	/**
	 * Serialize room booked list.
	 *
	 * @param room_list the room list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void serialize_RoomBookedList(List<Room> room_list) throws IOException
	{
        ObjectOutputStream outFile=null;
        try
        {
        	outFile=new ObjectOutputStream(new FileOutputStream("./src/RoomBookedList"));
        	outFile.writeObject(room_list);
        }
        finally
        {
        	outFile.close();
        }
	}
	
	/**
	 * Deserialize adminlist.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static List<Admin> deserialize_adminlist() throws IOException, ClassNotFoundException  //deserialize
	{
        ObjectInputStream inFile=null;
        try
        {
        	inFile=new ObjectInputStream(new FileInputStream("./src/AdminList"));
        	List<Admin> list=new ArrayList<Admin>();
        	list=(List<Admin>) inFile.readObject();
        	return list;
        }
        finally
        {
        	inFile.close();
        	
        }
	}
	
	/**
	 * Deserialize facultylist.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static List<Faculty> deserialize_facultylist() throws IOException, ClassNotFoundException  //deserialize
	{
        ObjectInputStream inFile=null;
        try
        {
        	inFile=new ObjectInputStream(new FileInputStream("./src/FacultyList"));
        	List<Faculty> list=new ArrayList<Faculty>();
        	list=(List<Faculty>) inFile.readObject();
        	return list;
        }
        finally
        {
        	inFile.close();
        	
        }
	}
	
	/**
	 * Deserialize studentlist.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static List<Student> deserialize_studentlist() throws IOException, ClassNotFoundException  //deserialize
	{
        ObjectInputStream inFile=null;
        try
        {
        	inFile=new ObjectInputStream(new FileInputStream("./src/StudentList"));
        	List<Student> list=new ArrayList<Student>();
        	list=(List<Student>) inFile.readObject();
        	return list;
        }
        finally
        {
        	inFile.close();
        	
        }
	}
	
	/**
	 * Deserialize room booked list.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static List<Room> deserialize_RoomBookedList() throws IOException, ClassNotFoundException  //deserialize
	{
        ObjectInputStream inFile=null;
        try
        {
        	inFile=new ObjectInputStream(new FileInputStream("./src/RoomBookedList"));
        	List<Room> list=new ArrayList<Room>();
        	list=(List<Room>) inFile.readObject();
        	return list;
        }
        finally
        {
        	inFile.close();
        	
        }
	}
	
	/**
	 * Serialize room req.
	 *
	 * @param room_list the room list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void serialize_RoomReq(List<Room> room_list) throws IOException
	{
        ObjectOutputStream outFile=null;
        try
        {
        	outFile=new ObjectOutputStream(new FileOutputStream("./src/RoomRequestList"));
        	outFile.writeObject(room_list);
        }
        finally
        {
        	outFile.close();
        }
	}
	
	/**
	 * Deserialize room req.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static List<Room> deserialize_RoomReq() throws IOException, ClassNotFoundException  //deserialize
	{
        ObjectInputStream inFile=null;
        File f=new File("./src/RoomRequestList");
        try
        {
        	if (!f.exists())
        	{
        		return new ArrayList<Room>();
        	}
        	inFile=new ObjectInputStream(new FileInputStream("./src/RoomRequestList"));
        	List<Room> list=new ArrayList<Room>();
        	list=(List<Room>) inFile.readObject();
        	return list;
        }
        finally
        {
        	inFile.close();
        	
        }
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static void main(final String []args) throws IOException, ClassNotFoundException
	{
		launch(args);
	}
	
	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void start(final Stage primaryStage) throws Exception 
    {
		final ImageView imageView = new ImageView(new Image(new FileInputStream("Images/IIITDeve.jpg")));
	    imageView.setFitHeight(745);
		imageView.setFitWidth(1305);
		
		Text text=new Text();
		text.setText("Welcome To IIITD");
		text.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 47.0));
		text.setFill(Color.FLORALWHITE);
	    text.setX(100);
	    text.setY(300);
	    
	    Text text2=new Text();
	    text2.setText("ClassRoom Booking Portal");
	    text2.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 47.0));
		text2.setFill(Color.FLORALWHITE);
	    text2.setX(40);
	    text2.setY(360);
	    
	    Pane pane=new Pane();
	    pane.getChildren().addAll(text, text2);
	    
	    final DropShadow shadow=new DropShadow();
	    
	    final Button login=new Button();
	    login.setText("Login");
	    login.setStyle("-fx-font: 25 arial; -fx-base: DARKSLATEBLUE; -fx-background-radius: 30; -fx-border-color: DARKSLATEBLUE; -fx-border-radius: 30;");
	    login.setLayoutX(120);
	    login.setLayoutY(440);
	    login.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() 
	    {
	    	@Override
	        public void handle(MouseEvent e) 
	    	{
	    		login.setEffect(shadow);
	        }
	    });
	    login.addEventHandler(MouseEvent.MOUSE_EXITED,new EventHandler<MouseEvent>() 
	    {
	    	@Override
	    	public void handle(MouseEvent e) 
	    	{
	    		login.setEffect(null);
	    	}
	    });
	    pane.getChildren().add(login);
	    
	    Text text3=new Text();
	    text3.setText("Already a member?");
	    text3.setFont(Font.font("Abyssinica SIL", 18.0));
	    text3.setFill(Color.BLACK);
	    text3.setX(90);
	    text3.setY(430);
	    pane.getChildren().add(text3);
	    
	    final Button signup=new Button();
	    signup.setText("Signup");
	    signup.setStyle("-fx-font: 25 arial; -fx-base: silver; -fx-background-radius: 30; -fx-border-color: silver; -fx-border-radius: 30;");
	    signup.setLayoutX(340);
	    signup.setLayoutY(440);
	    signup.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() 
	    {
	    	@Override
	        public void handle(MouseEvent e) 
	    	{
	    		signup.setEffect(shadow);
	        }
	    });
	    signup.addEventHandler(MouseEvent.MOUSE_EXITED,new EventHandler<MouseEvent>() 
	    {
	    	@Override
	    	public void handle(MouseEvent e) 
	    	{
	    		signup.setEffect(null);
	    	}
	    });
	    pane.getChildren().add(signup);
	    
	    Text text4=new Text();
	    text4.setText("Not a registered user yet?");
	    text4.setFont(Font.font("Abyssinica SIL", 18.0));
	    text4.setFill(Color.BLACK);
	    text4.setX(300);
	    text4.setY(430);
	    pane.getChildren().add(text4);
	    
	    
        StackPane layout = new StackPane();
	    layout.getChildren().addAll(imageView, pane);
	    final Scene scene=new Scene(layout);

	    login.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            scene.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    login.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	            scene.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    login.setOnAction(new EventHandler<ActionEvent>() 
	    {
			@Override
			public void handle(ActionEvent e) 
			{
				Login login=new Login();
	    		try {
					login.login(primaryStage);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
	    });
	    signup.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            scene.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    signup.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	            scene.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    signup.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		Signup signup=new Signup();
	    		try 
	    		{
					signup.signup(primaryStage);
				} 
	    		catch (FileNotFoundException e1)
	    		{
					e1.printStackTrace();
				}
	    	}
	    });
	    primaryStage.setTitle("ClassRoom Booking Portal | IIIT-D");
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}
}

/**
 * The Class User.
 */
class User extends mainPage implements Serializable
{
	
	/** The room name. */
	private transient String room_name="", Sdate="", time_slot="";
    
    /** The booked. */
    private volatile transient boolean booked=false;
    
    /** The week day. */
    private transient int week_day=0;   
	
	/** The list. */
	private volatile List<Room> list;
	
	/** The room req. */
	public volatile List<Room> room_req;
	
	/** The course 1. */
	public CourseDatabase course1=new CourseDatabase();
	
	/**
	 * Book room.
	 *
	 * @param window the window
	 * @param masterpane the masterpane
	 * @param user the user
	 */
	public synchronized void book_room(final Pane window, final Pane masterpane, final String user)
	{
		try
		{
			try
			{
				list=deserialize_RoomBookedList();
				room_req=deserialize_RoomReq();
			} 
			catch (ClassNotFoundException | IOException e) 
			{
				e.printStackTrace();
			}
			final DatePicker datePicker = new DatePicker();
    		datePicker.setLayoutX(60);
    		datePicker.setLayoutY(40);
    		datePicker.setPrefSize(200, 40);
    		
    		final String pattern = "dd-MM-yyyy";
    		final String pattern1="yyyyMMdd";
    		
    		datePicker.setPromptText(pattern.toLowerCase());
    		final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
    		final DateTimeFormatter dateFormatter1 = DateTimeFormatter.ofPattern(pattern1);
    		
    		final ContextMenu dateValidator=new ContextMenu();
	        dateValidator.hide();
	        dateValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;");
	        dateValidator.getItems().clear();
    		datePicker.setConverter(new StringConverter<LocalDate>()
    		{
    		     @Override 
    		     public String toString(LocalDate date)
    		     {
    		         if (date != null) 
    		        	 return dateFormatter.format(date); 
    		         else 
    		        	 return "";
    		     }

    		     @Override 
    		     public LocalDate fromString(String string)
    		     {
    		         if (string != null && !string.isEmpty()) 
    		             return LocalDate.parse(string, dateFormatter);
    		         else 
    		             return null;
    		     }
    		 });
    		datePicker.setOnAction(new EventHandler<ActionEvent>()
    	    {
    			@Override
				public void handle(ActionEvent arg0) 
				{
					LocalDate date=datePicker.getValue();
					LocalDate now=LocalDate.now();
					if (Integer.parseInt(now.format(dateFormatter1))<=Integer.parseInt(date.format(dateFormatter1)))
					{
						dateValidator.hide();
						dateValidator.getItems().clear();
					}
					else
					{
						dateValidator.getItems().clear();
	                    MenuItem m2=new MenuItem("Please Select Correct Date");
	                    m2.setStyle("-fx-text-fill: red");
	                    dateValidator.getItems().add(m2);
	                    dateValidator.show(datePicker, Side.BOTTOM, 0, 0);
					}
    			}
    	    });
    		
    		final ComboBox<String> myComboBox=new ComboBox<String>();
    		myComboBox.setEditable(true);
    		myComboBox.setPromptText("Room Capacity");
    		myComboBox.getItems().add("Less than 30");
    		myComboBox.getItems().add("Between 30 and 60");
    		myComboBox.getItems().add("More than 60 and less than 300");
    		myComboBox.setPrefHeight(40);
    		HBox typebox=new HBox();
    		typebox.setLayoutX(340);
    		typebox.setLayoutY(40);
    		typebox.getChildren().add(myComboBox);

    	    final ContextMenu roomValidator=new ContextMenu();
	        roomValidator.hide();
	        roomValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;");
	        roomValidator.getItems().clear();
	        
    		final ComboBox<String> myComboBox1=new ComboBox<String>();
    		myComboBox1.setEditable(true); 
    		myComboBox1.setPromptText("Room List");
    		myComboBox1.getItems().add("C01");
    		myComboBox1.getItems().add("C11");
    		myComboBox1.getItems().add("C21");
    		myComboBox1.getItems().add("C02");
    		myComboBox1.getItems().add("C03");
    		myComboBox1.getItems().add("C04");
    		myComboBox1.getItems().add("C12");
    		myComboBox1.getItems().add("C13");
    		myComboBox1.getItems().add("C14");
    		myComboBox1.getItems().add("C22");
    		myComboBox1.getItems().add("C23");
    		myComboBox1.getItems().add("C24");
    		myComboBox1.getItems().add("LR1");
    		myComboBox1.getItems().add("LR2");
    		myComboBox1.getItems().add("LR3");
    		myComboBox1.getItems().add("L21");
    		myComboBox1.getItems().add("L22");
    		myComboBox1.getItems().add("L23");
    		myComboBox1.getItems().add("L33");
    		myComboBox1.getItems().add("SCL");
    		
    		myComboBox1.setPrefHeight(40);
    		HBox typebox1=new HBox();
    		typebox1.setLayoutX(670);
    		typebox1.setLayoutY(40);
    		typebox1.getChildren().add(myComboBox1);
    		
    		final String pattern2="HH:mm";
    		
    		final DateTimeFormatter dateFormatter2 = DateTimeFormatter.ofPattern(pattern2);
    		
    		GridPane grid=new GridPane();
    		grid.setLayoutX(270);
    		grid.setLayoutY(180);
    		grid.setHgap(40);
    		grid.setVgap(40);
    		
    		Label start=new Label("Enter Start Time");
    		start.setFont(Font.font("Arial", FontWeight.BOLD, 17));
    		grid.add(start, 0, 0);
    		
    		final TextField uStart=new TextField();
    		uStart.setPromptText("HH:mm (24-hr format)");
    		grid.add(uStart, 1, 0, 3, 1);
    		
    		Label end=new Label("Enter End Time");
    		end.setFont(Font.font("Arial", FontWeight.BOLD, 17));
    		grid.add(end, 0, 1);
    		
    		final TextField uEnd=new TextField();
    		uEnd.setPromptText("hh:mm (24-hr format)");
    		grid.add(uEnd, 1, 1, 3, 1);
    		
    		Label purpose=new Label("Enter Purpose");
    		purpose.setFont(Font.font("Arial", FontWeight.BOLD, 17));
    		grid.add(purpose, 0, 2);
    		
    		final TextField uPurpose=new TextField();
    		uPurpose.setPromptText("Purpose");
    		grid.add(uPurpose, 1, 2, 3, 1);
    		
    		Label aud=new Label("Enter Audience");
    		aud.setFont(Font.font("Arial", FontWeight.BOLD, 17));
    		grid.add(aud, 0, 3);
    		
    		final TextField uAud=new TextField();
    		uAud.setPromptText("Audience");
    		grid.add(uAud, 1, 3, 3, 1);
    		
    		final ContextMenu startValidator=new ContextMenu();
	        startValidator.hide();
	        startValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;");
	        startValidator.getItems().clear();
	        
	        final ContextMenu endValidator=new ContextMenu();
	        endValidator.hide();
	        endValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;");
	        endValidator.getItems().clear();
	        
    		final Button avail=new Button("Check Availabilty");
    		avail.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 22));
    		avail.setTextFill(Color.GHOSTWHITE);
    		avail.setStyle("-fx-background-color: #000000,linear-gradient(#7ebcea, #2f4b8f),linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);");
    		grid.add(avail, 0, 5);
    		
    		final Button booking=new Button();
    		if (!user.equalsIgnoreCase("Student")) 
    		{
    			booking.setText("Book Room");
        		booking.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 22));
        		booking.setTextFill(Color.GHOSTWHITE);
        		booking.setStyle("-fx-background-color: linear-gradient(#f0ff35, #a9ff00),radial-gradient(center 50% -40%, radius 200%, #b8ee36 45%, #80c800 50%);");
        		grid.add(booking, 2, 5);
    		}
    		else
    		{
    			booking.setText("Send Request");
        		booking.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 22));
        		booking.setTextFill(Color.GHOSTWHITE);
        		booking.setStyle("-fx-background-color: linear-gradient(#f0ff35, #a9ff00),radial-gradient(center 50% -40%, radius 200%, #b8ee36 45%, #80c800 50%);");
        		grid.add(booking, 2, 5);
    		}
    		final DropShadow shadow=new DropShadow();
    		
    		avail.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() 
    	    {
    			@Override
		        public void handle(MouseEvent e) 
		    	{
		    		avail.setEffect(shadow);
		        }
    		});
    		avail.addEventHandler(MouseEvent.MOUSE_EXITED,new EventHandler<MouseEvent>() 
    		{
    			@Override
		    	public void handle(MouseEvent e) 
		    	{
		    		avail.setEffect(null);
		    	}
    		});
    		avail.setOnMouseEntered(new EventHandler() 
    	    {
    	    	@Override
    			public void handle(Event me) {
    	            window.setCursor(Cursor.HAND); //Change cursor to hand
    			}
    	    });
    	    avail.setOnMouseExited(new EventHandler() 
    	    {
    	    	@Override
    	        public void handle(Event me) {
    	    		window.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
    	        }
    	    });
    	    
    	    booking.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() 
		    {
    	    	@Override
		        public void handle(MouseEvent e) 
		    	{
		    		booking.setEffect(shadow);
		        }
		    });
		    booking.addEventHandler(MouseEvent.MOUSE_EXITED,new EventHandler<MouseEvent>() 
		    {
		    	@Override
		    	public void handle(MouseEvent e) 
		    	{
		    		booking.setEffect(null);
		    	}
		    });
		    booking.setOnMouseEntered(new EventHandler() 
		    {
		    	@Override
    			public void handle(Event me) {
    	            window.setCursor(Cursor.HAND); //Change cursor to hand
    			}
		    });
		    booking.setOnMouseExited(new EventHandler() 
		    {
		    	@Override
    	        public void handle(Event me) {
    	    		window.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
    	        }
		    });
		    
    	    final Date date1=new Date(); 
    	    

	        final ContextMenu availValidator=new ContextMenu();
	        availValidator.hide();
	        availValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;");
	        availValidator.getItems().clear();
	        
	        final ContextMenu bookValidator=new ContextMenu();
	        bookValidator.hide();
	        bookValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;");
	        bookValidator.getItems().clear();
	        
    	    avail.setOnAction(new EventHandler<ActionEvent>()
    	   	{
    	    	@Override
    	    	public void handle(ActionEvent arg0)
    	    	{
    	    		if (!uStart.getText().matches("\\d{2}:\\d{2}"))
    	    		{
    	    			startValidator.getItems().clear();
	                    MenuItem m2=new MenuItem("Please Enter Time In Specified Format");
	                    m2.setStyle("-fx-text-fill: red");
	                    startValidator.getItems().add(m2);
	                    startValidator.show(uStart, Side.BOTTOM, 0, 0);
    	    		}
    	    		else if (Integer.parseInt(datePicker.getValue().format(dateFormatter1))==Integer.parseInt(LocalDate.now().format(dateFormatter1)) && Integer.parseInt(new SimpleDateFormat("HHmm").format(date1))>=Integer.parseInt(uStart.getText().replace(":", "")))
    	    		{
    	    			startValidator.getItems().clear();
	                    MenuItem m2=new MenuItem("Please Enter Correct Time");
	                    m2.setStyle("-fx-text-fill: red");
	                    startValidator.getItems().add(m2);
	                    startValidator.show(uStart, Side.BOTTOM, 0, 0);
    	    		}
    	    		if (!uEnd.getText().matches("\\d{2}:\\d{2}"))
    	    		{
    	    			endValidator.getItems().clear();
	                    MenuItem m3=new MenuItem("Please Enter Time In Specified Format");
	                    m3.setStyle("-fx-text-fill: red");
	                    endValidator.getItems().add(m3);
	                    endValidator.show(uEnd, Side.BOTTOM, 0, 0);
    	    		}
    	    		else if (Integer.parseInt(datePicker.getValue().format(dateFormatter1))==Integer.parseInt(LocalDate.now().format(dateFormatter1)) && Integer.parseInt(new SimpleDateFormat("HHmm").format(date1))>=Integer.parseInt(uEnd.getText().replace(":", "")))
    	    		{
    	    			endValidator.getItems().clear();
	                    MenuItem m3=new MenuItem("Please Enter Correct Time");
	                    m3.setStyle("-fx-text-fill: red");
	                    endValidator.getItems().add(m3);
	                    endValidator.show(uEnd, Side.BOTTOM, 0, 0);
    	    		}
    	    		try
                    {
                    	room_name=myComboBox1.getSelectionModel().getSelectedItem().toString();
                    }
                    catch (NullPointerException ex)
                    {
                    	roomValidator.getItems().clear();
	                    MenuItem m4=new MenuItem("Please Select A Room");
	                    m4.setStyle("-fx-text-fill: red");
	                    roomValidator.getItems().add(m4);
	                    roomValidator.show(myComboBox1, Side.BOTTOM, 10, 0);
                    }
    	    		if (uStart.getText().matches("\\d{2}:\\d{2}") && uEnd.getText().matches("\\d{2}:\\d{2}") && !(Integer.parseInt(datePicker.getValue().format(dateFormatter1))==Integer.parseInt(LocalDate.now().format(dateFormatter1)) && Integer.parseInt(new SimpleDateFormat("HHmm").format(date1))>=Integer.parseInt(uStart.getText().replace(":", ""))) && !(Integer.parseInt(datePicker.getValue().format(dateFormatter1))==Integer.parseInt(LocalDate.now().format(dateFormatter1)) && Integer.parseInt(new SimpleDateFormat("HHmm").format(date1))>=Integer.parseInt(uEnd.getText().replace(":", ""))))
    	    		{
    	    			LocalDate date=datePicker.getValue();
	    				Sdate=date.format(dateFormatter).toString();
	    				time_slot=uStart.getText().replace(":", "")+"-"+uEnd.getText().replace(":", "");
	    				week_day=0;
	    				switch(date.getDayOfWeek().toString())
	    				{
	    				case "MONDAY": week_day=1;
	    				break;
	    				case "TUESDAY": week_day=2;
	    				break;
	    				case "WEDNESDAY": week_day=3;
	    				break;
	    				case "THURSDAY": week_day=4;
	    				break;
	    				case "FRIDAY": week_day=5;
	    				break;
	    				case "SATURDAY": week_day=6;
	    				break;
	    				case "SUNDAY": week_day=7;
	    				}
	    				for (int i=0; i<list.size(); i++)
	    				{
    	    				String []time1=list.get(i).gettime().split("-");
    	    				String []time2=time_slot.split("-");
	    					if (list.get(i).getRoom().equals(room_name))
	    					{
	    						if (list.get(i).getDay()==week_day)
	    						{
	    							if (list.get(i).getdate()==null || list.get(i).getdate().equals(Sdate))
	    							{
    	    							if (list.get(i).gettime().equals(time_slot))
    	    							{
    	    								availValidator.getItems().clear();
	    				                    MenuItem m3=new MenuItem("This Slot is Booked.");
	    				                    m3.setStyle("-fx-text-fill: red");
	    				                    availValidator.getItems().add(m3);
	    				                    availValidator.show(avail, Side.BOTTOM, 0, 0);
	    				                    booked=true;
	    				                    break;
    	    							}
    	    							else if ((Integer.parseInt(time2[0])>=Integer.parseInt(time1[0]) && Integer.parseInt(time2[0])<Integer.parseInt(time1[1])) || (Integer.parseInt(time2[1])>Integer.parseInt(time1[0]) && Integer.parseInt(time2[1])<=Integer.parseInt(time1[1])))
    	    							{
    	    								availValidator.getItems().clear();
	    				                    MenuItem m3=new MenuItem("This Slot is Booked.");
	    				                    m3.setStyle("-fx-text-fill: red");
	    				                    availValidator.getItems().add(m3);
	    				                    availValidator.show(avail, Side.BOTTOM, 0, 0);
	    				                    booked=true;
	    				                    break;
    	    							}
	    							}
	    						}
	    					}
	    				}
	    				if (booked==false)
	    				{
	    					availValidator.getItems().clear();
		                    MenuItem m3=new MenuItem("This Slot is Available.");
		                    m3.setStyle("-fx-text-fill: ghostwhite");
		                    availValidator.getItems().add(m3);
		                    availValidator.show(avail, Side.BOTTOM, 0, 0);
	    				}
    	    		}
    	    	}
      		});
    	    
    	    booking.setOnAction(new EventHandler<ActionEvent>()
		    {
    	    	@Override
    	    	public void handle(ActionEvent arg0)
    	    	{
    	    		if (booked==true)
    	    		{
    	    			bookValidator.getItems().clear();
	                    MenuItem m3=new MenuItem("This Slot is Booked.");
	                    m3.setStyle("-fx-text-fill: red");
	                    bookValidator.getItems().add(m3);
	                    bookValidator.show(booking, Side.BOTTOM, 0, 0);
    	    		}
    	    		else if (booked==false && (user.equalsIgnoreCase("Admin") || user.equalsIgnoreCase("Faculty") || user.equalsIgnoreCase("Student Request")))
    				{   
    					Room room=new Room();
    					room.setcourse(uPurpose.getText());
    					room.setday(week_day);
    					room.setdate(Sdate);
    					room.settime(time_slot);
    					room.setroom(room_name);
    					room.setaudience(uAud.getText());
    					room.setuser(user);
    					
    					bookValidator.getItems().clear();
	                    MenuItem m4=new MenuItem("Congratulations Slot Booked.");
	                    m4.setStyle("-fx-text-fill: ghostwhite");
	                    bookValidator.getItems().add(m4);
	                    bookValidator.show(booking, Side.BOTTOM, 0, 0);
    					list.add(room);
    					try 
    					{
    						serialize_RoomBookedList(list);
    					} 
    					catch (IOException e)
    					{
    						e.printStackTrace();
    					}
    				}
    	    		else if (booked==false && user.contains("Student"))
    	    		{
    	    			Room room=new Room();
    					room.setcourse(uPurpose.getText());
    					room.setday(week_day);
    					room.setdate(Sdate);
    					room.settime(time_slot);
    					room.setroom(room_name);
    					room.setaudience(uAud.getText());
    					room.setuser(user);
    					
    					bookValidator.getItems().clear();
	                    MenuItem m4=new MenuItem("Request Sent.");
	                    m4.setStyle("-fx-text-fill: red");
	                    bookValidator.getItems().add(m4);
	                    bookValidator.show(booking, Side.BOTTOM, 0, 0);
    	    			room_req.add(room);
    	    			try 
    	    			{
							serialize_RoomReq(room_req);
						} 
    	    			catch (IOException e)
    	    			{
							e.printStackTrace();
						}
    	    		}
    	    	}
		    });
    	    uStart.focusedProperty().addListener(new ChangeListener<Boolean>() 
		            {
		            	@Override
		                public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
		    			{
		                    if (newPropertyValue) 
		                    {
		                        startValidator.hide();
		                    }
		                }
		             });
		    uEnd.focusedProperty().addListener(new ChangeListener<Boolean>() 
		            {
		            	@Override
		                public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
		    			{
		                    if (newPropertyValue) 
		                    {
		                        endValidator.hide();
		                    }
		                }
		             });
		    datePicker.focusedProperty().addListener(new ChangeListener<Boolean>() 
		            {
		            	@Override
		                public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
		    			{
		                    if (newPropertyValue) 
		                    {
		                        startValidator.hide();
		                    }
		                }
		             });
		    booking.focusedProperty().addListener(new ChangeListener<Boolean>() 
		            {
		            	@Override
		                public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
		    			{
		                    if (newPropertyValue) 
		                    {
		                        endValidator.hide();
		                    }
		                }
		             });
		    avail.focusedProperty().addListener(new ChangeListener<Boolean>() 
		            {
		            	@Override
		                public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
		    			{
		                    if (newPropertyValue) 
		                    {
		                        startValidator.hide();
		                    }
		                }
		             });
		    myComboBox1.focusedProperty().addListener(new ChangeListener<Boolean>() 
		            {
		            	@Override
		                public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
		    			{
		                    if (newPropertyValue) 
		                    {
		                        endValidator.hide();
		                    }
		                }
		             });
    	    booked=false;
    	    final Pane frame=new Pane();
    		frame.setStyle("-fx-background-color: darkgray;");
    		frame.setLayoutX(20);
        	frame.setLayoutY(20);
        	frame.setPrefSize(1010, 635);
        	frame.getChildren().add(datePicker);
        	frame.getChildren().add(typebox);
        	frame.getChildren().add(typebox1);
        	frame.getChildren().add(grid);
    		window.getChildren().add(frame);
        	masterpane.getChildren().add(window);
    		dateValidator.hide();
    		startValidator.hide();
    		endValidator.hide();
    		availValidator.hide();
    		bookValidator.hide();
    		
    		
		}
		catch (IllegalArgumentException | NullPointerException e1)
		{
			System.out.print("");
		}
	}
	
	/**
	 * Avail room.
	 *
	 * @param window the window
	 * @param masterpane the masterpane
	 */
	public synchronized void avail_room(final Pane window, final Pane masterpane)
	{
		try
		{
			try
			{
				list=deserialize_RoomBookedList();
			} 
			catch (ClassNotFoundException | IOException e) 
			{
				e.printStackTrace();
			}
			
			final DatePicker datePicker = new DatePicker();
			datePicker.setLayoutX(60);
			datePicker.setLayoutY(40);
			datePicker.setPrefSize(200, 40);
			
			final String pattern = "dd-MM-yyyy";
			final String pattern1="yyyyMMdd";
			
			datePicker.setPromptText(pattern.toLowerCase());
			final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
			final DateTimeFormatter dateFormatter1 = DateTimeFormatter.ofPattern(pattern1);
			
			final ContextMenu dateValidator=new ContextMenu();
	        dateValidator.hide();
	        dateValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;");
	        dateValidator.getItems().clear();
			datePicker.setConverter(new StringConverter<LocalDate>()
			{
			     @Override 
			     public String toString(LocalDate date)
			     {
			         if (date != null) 
			        	 return dateFormatter.format(date); 
			         else 
			        	 return "";
			     }

			     @Override 
			     public LocalDate fromString(String string)
			     {
			         if (string != null && !string.isEmpty()) 
			             return LocalDate.parse(string, dateFormatter);
			         else 
			             return null;
			     }
			 });
			
			final ComboBox<String> myComboBox=new ComboBox<String>();
			myComboBox.setEditable(true); 
			myComboBox.setPromptText("Room Capacity");
			myComboBox.getItems().add("Less than 30");
			myComboBox.getItems().add("Between 30 and 60");
			myComboBox.getItems().add("More than 60 and less than 300");
			myComboBox.setPrefHeight(40);
			HBox typebox=new HBox();
			typebox.setLayoutX(340);
			typebox.setLayoutY(40);
			typebox.getChildren().add(myComboBox);
			

		    final ContextMenu roomValidator=new ContextMenu();
	        roomValidator.hide();
	        roomValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;");
	        roomValidator.getItems().clear();
	        
			final ComboBox<String> myComboBox1=new ComboBox<String>();
			myComboBox1.setEditable(true); 
			myComboBox1.setPromptText("Room List");
			myComboBox1.getItems().add("C01");
			myComboBox1.getItems().add("C11");
			myComboBox1.getItems().add("C21");
			myComboBox1.getItems().add("C02");
			myComboBox1.getItems().add("C03");
			myComboBox1.getItems().add("C04");
			myComboBox1.getItems().add("C12");
			myComboBox1.getItems().add("C13");
			myComboBox1.getItems().add("C14");
			myComboBox1.getItems().add("C22");
			myComboBox1.getItems().add("C23");
			myComboBox1.getItems().add("C24");
			myComboBox1.getItems().add("LR1");
			myComboBox1.getItems().add("LR2");
			myComboBox1.getItems().add("LR3");
			myComboBox1.getItems().add("L21");
			myComboBox1.getItems().add("L22");
			myComboBox1.getItems().add("L23");
			myComboBox1.getItems().add("L33");
			myComboBox1.getItems().add("SCL");
			
			myComboBox1.setPrefHeight(40);
			HBox typebox1=new HBox();
			typebox1.setLayoutX(670);
			typebox1.setLayoutY(40);
			typebox1.getChildren().add(myComboBox1);
			
			final String pattern2="HH:mm";
			
			final DateTimeFormatter dateFormatter2 = DateTimeFormatter.ofPattern(pattern2);
			
			GridPane grid=new GridPane();
			grid.setLayoutX(270);
			grid.setLayoutY(180);
			grid.setHgap(40);
			grid.setVgap(40);
			
			Label start=new Label("Enter Start Time");
			start.setFont(Font.font("Arial", FontWeight.BOLD, 17));
			grid.add(start, 0, 0);
			
			final TextField uStart=new TextField();
			uStart.setPromptText("HH:mm (24-hr format)");
			grid.add(uStart, 1, 0, 3, 1);
			
			Label end=new Label("Enter End Time");
			end.setFont(Font.font("Arial", FontWeight.BOLD, 17));
			grid.add(end, 0, 1);
			
			final TextField uEnd=new TextField();
			uEnd.setPromptText("hh:mm (24-hr format)");
			grid.add(uEnd, 1, 1, 3, 1);
			
			Label purpose=new Label("Enter Purpose");
			purpose.setFont(Font.font("Arial", FontWeight.BOLD, 17));
			grid.add(purpose, 0, 2);
			
			final TextField uPurpose=new TextField();
			uPurpose.setPromptText("Purpose");
			grid.add(uPurpose, 1, 2, 3, 1);
			
			Label aud=new Label("Enter Audience");
			aud.setFont(Font.font("Arial", FontWeight.BOLD, 17));
			grid.add(aud, 0, 3);
			
			final TextField uAud=new TextField();
			uAud.setPromptText("Audience");
			grid.add(uAud, 1, 3, 3, 1);
			
			final ContextMenu startValidator=new ContextMenu();
	        startValidator.hide();
	        startValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;");
	        startValidator.getItems().clear();
	        
	        final ContextMenu endValidator=new ContextMenu();
	        endValidator.hide();
	        endValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;");
	        endValidator.getItems().clear();
	        
			final Button avail=new Button("Check Availabilty");
			avail.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 22));
			avail.setTextFill(Color.GHOSTWHITE);
			avail.setStyle("-fx-background-color: #000000,linear-gradient(#7ebcea, #2f4b8f),linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);");
			grid.add(avail, 0, 5);
			
			final DropShadow shadow=new DropShadow();
			
			avail.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() 
		    {
				@Override
		        public void handle(MouseEvent e) 
		    	{
		    		avail.setEffect(shadow);
		        }
			});
			avail.addEventHandler(MouseEvent.MOUSE_EXITED,new EventHandler<MouseEvent>() 
			{
				@Override
		    	public void handle(MouseEvent e) 
		    	{
		    		avail.setEffect(null);
		    	}
			});
			avail.setOnMouseEntered(new EventHandler() 
		    {
		    	@Override
				public void handle(Event me) {
		            window.setCursor(Cursor.HAND); //Change cursor to hand
				}
		    });
		    avail.setOnMouseExited(new EventHandler() 
		    {
		    	@Override
		        public void handle(Event me) {
		    		window.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
		        }
		    });
		    
		    final Date date1=new Date(); 
		    
	        final ContextMenu availValidator=new ContextMenu();
	        availValidator.hide();
	        availValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;");
	        availValidator.getItems().clear();
	        
	        
		    avail.setOnAction(new EventHandler<ActionEvent>()
		   	{
		    	@Override
		    	public void handle(ActionEvent arg0)
		    	{
		    		if (!uStart.getText().matches("\\d{2}:\\d{2}"))
		    		{
		    			startValidator.getItems().clear();
	                    MenuItem m2=new MenuItem("Please Enter Time In Specified Format");
	                    m2.setStyle("-fx-text-fill: red");
	                    startValidator.getItems().add(m2);
	                    startValidator.show(uStart, Side.BOTTOM, 0, 0);
		    		}
		    		else if (Integer.parseInt(datePicker.getValue().format(dateFormatter1))==Integer.parseInt(LocalDate.now().format(dateFormatter1)) && Integer.parseInt(new SimpleDateFormat("HHmm").format(date1))>=Integer.parseInt(uStart.getText().replace(":", "")))
		    		{
		    			startValidator.getItems().clear();
	                    MenuItem m2=new MenuItem("Please Enter Correct Time");
	                    m2.setStyle("-fx-text-fill: red");
	                    startValidator.getItems().add(m2);
	                    startValidator.show(uStart, Side.BOTTOM, 0, 0);
		    		}
		    		if (!uEnd.getText().matches("\\d{2}:\\d{2}"))
		    		{
		    			endValidator.getItems().clear();
	                    MenuItem m3=new MenuItem("Please Enter Time In Specified Format");
	                    m3.setStyle("-fx-text-fill: red");
	                    endValidator.getItems().add(m3);
	                    endValidator.show(uEnd, Side.BOTTOM, 0, 0);
		    		}
		    		else if (Integer.parseInt(datePicker.getValue().format(dateFormatter1))==Integer.parseInt(LocalDate.now().format(dateFormatter1)) && Integer.parseInt(new SimpleDateFormat("HHmm").format(date1))>=Integer.parseInt(uEnd.getText().replace(":", "")))
		    		{
		    			endValidator.getItems().clear();
	                    MenuItem m3=new MenuItem("Please Enter Correct Time");
	                    m3.setStyle("-fx-text-fill: red");
	                    endValidator.getItems().add(m3);
	                    endValidator.show(uEnd, Side.BOTTOM, 0, 0);
		    		}
					datePicker.setOnAction(new EventHandler<ActionEvent>()
					{
						@Override
						public void handle(ActionEvent arg0) 
						{
							LocalDate date=datePicker.getValue();
							LocalDate now=LocalDate.now();
							if (Integer.parseInt(now.format(dateFormatter1))<=Integer.parseInt(date.format(dateFormatter1)))
							{
								dateValidator.hide();
								dateValidator.getItems().clear();
							}
							else
							{
								dateValidator.getItems().clear();
			                    MenuItem m2=new MenuItem("Please Select Correct Date");
			                    m2.setStyle("-fx-text-fill: red");
			                    dateValidator.getItems().add(m2);
			                    dateValidator.show(datePicker, Side.BOTTOM, 0, 0);
							}
						}
					});
		    		try
	                {
	                	room_name=myComboBox1.getSelectionModel().getSelectedItem().toString();
	                }
	                catch (NullPointerException ex)
	                {
	                	roomValidator.getItems().clear();
	                    MenuItem m4=new MenuItem("Please Select A Room");
	                    m4.setStyle("-fx-text-fill: red");
	                    roomValidator.getItems().add(m4);
	                    roomValidator.show(myComboBox1, Side.BOTTOM, 10, 0);
	                }
		    		if (uStart.getText().matches("\\d{2}:\\d{2}") && uEnd.getText().matches("\\d{2}:\\d{2}") && !(Integer.parseInt(datePicker.getValue().format(dateFormatter1))==Integer.parseInt(LocalDate.now().format(dateFormatter1)) && Integer.parseInt(new SimpleDateFormat("HHmm").format(date1))>=Integer.parseInt(uStart.getText().replace(":", ""))) && !(Integer.parseInt(datePicker.getValue().format(dateFormatter1))==Integer.parseInt(LocalDate.now().format(dateFormatter1)) && Integer.parseInt(new SimpleDateFormat("HHmm").format(date1))>=Integer.parseInt(uEnd.getText().replace(":", ""))))
		    		{
		    			LocalDate date=datePicker.getValue();
	    				Sdate=date.format(dateFormatter).toString();
	    				time_slot=uStart.getText().replace(":", "")+"-"+uEnd.getText().replace(":", "");
	    				week_day=0;
	    				switch(date.getDayOfWeek().toString())
	    				{
	    				case "MONDAY": week_day=1;
	    				break;
	    				case "TUESDAY": week_day=2;
	    				break;
	    				case "WEDNESDAY": week_day=3;
	    				break;
	    				case "THURSDAY": week_day=4;
	    				break;
	    				case "FRIDAY": week_day=5;
	    				break;
	    				case "SATURDAY": week_day=6;
	    				break;
	    				case "SUNDAY": week_day=7;
	    				}
	    				for (int i=0; i<list.size(); i++)
	    				{
		    				String []time1=list.get(i).gettime().split("-");
		    				String []time2=time_slot.split("-");
	    					if (list.get(i).getRoom().equals(room_name))
	    					{
	    						if (list.get(i).getDay()==week_day)
	    						{
	    							if (list.get(i).getdate()==null || list.get(i).getdate().equals(Sdate))
	    							{
		    							if (list.get(i).gettime().equals(time_slot))
		    							{
		    								availValidator.getItems().clear();
	    				                    MenuItem m3=new MenuItem("This Slot is Booked.");
	    				                    m3.setStyle("-fx-text-fill: red");
	    				                    availValidator.getItems().add(m3);
	    				                    availValidator.show(avail, Side.BOTTOM, 0, 0);
	    				                    booked=true;
	    				                    break;
		    							}
		    							else if ((Integer.parseInt(time2[0])>=Integer.parseInt(time1[0]) && Integer.parseInt(time2[0])<Integer.parseInt(time1[1])) || (Integer.parseInt(time2[1])>Integer.parseInt(time1[0]) && Integer.parseInt(time2[1])<=Integer.parseInt(time1[1])))
		    							{
		    								availValidator.getItems().clear();
	    				                    MenuItem m3=new MenuItem("This Slot is Booked.");
	    				                    m3.setStyle("-fx-text-fill: red");
	    				                    availValidator.getItems().add(m3);
	    				                    availValidator.show(avail, Side.BOTTOM, 0, 0);
	    				                    booked=true;
	    				                    break;
		    							}
	    							}
	    						}
	    					}
	    				}
	    				if (booked==false)
	    				{
	    					availValidator.getItems().clear();
		                    MenuItem m3=new MenuItem("This Slot is Available.");
		                    m3.setStyle("-fx-text-fill: ghostwhite");
		                    availValidator.getItems().add(m3);
		                    availValidator.show(avail, Side.BOTTOM, 0, 0);
	    				}
		    		}
		    	}
	  		});
		    uStart.focusedProperty().addListener(new ChangeListener<Boolean>() 
		            {
		            	@Override
		                public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
		    			{
		                    if (newPropertyValue) 
		                    {
		                        startValidator.hide();
		                    }
		                }
		             });
		    uEnd.focusedProperty().addListener(new ChangeListener<Boolean>() 
		            {
		            	@Override
		                public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
		    			{
		                    if (newPropertyValue) 
		                    {
		                        endValidator.hide();
		                    }
		                }
		             });

		    datePicker.focusedProperty().addListener(new ChangeListener<Boolean>() 
		            {
		            	@Override
		                public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
		    			{
		                    if (newPropertyValue) 
		                    {
		                        startValidator.hide();
		                    }
		                }
		             });
		    avail.focusedProperty().addListener(new ChangeListener<Boolean>() 
		            {
		            	@Override
		                public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
		    			{
		                    if (newPropertyValue) 
		                    {
		                        startValidator.hide();
		                    }
		                }
		             });
		    myComboBox1.focusedProperty().addListener(new ChangeListener<Boolean>() 
		            {
		            	@Override
		                public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
		    			{
		                    if (newPropertyValue) 
		                    {
		                        endValidator.hide();
		                    }
		                }
		             });
		    booked=false;
		    final Pane frame=new Pane();
			frame.setStyle("-fx-background-color: darkgray;");
			frame.setLayoutX(20);
	    	frame.setLayoutY(20);
	    	frame.setPrefSize(1010, 635);
	    	frame.getChildren().add(datePicker);
	    	frame.getChildren().add(typebox);
	    	frame.getChildren().add(typebox1);
	    	frame.getChildren().add(grid);
			window.getChildren().add(frame);
	    	masterpane.getChildren().add(window);
	    	
			dateValidator.hide();
			startValidator.hide();
			endValidator.hide();
			availValidator.hide();
		}
		catch (IllegalArgumentException | NullPointerException e1)
		{
			System.out.print("");
		}
	}
	
	/**
	 * Logout.
	 *
	 * @param primaryStage the primary stage
	 * @param window the window
	 * @param masterpane the masterpane
	 */
	public void logout(final Stage primaryStage, final Pane window, final Pane masterpane)
	{
		try
		{
			Label log=new Label("Are You Sure You Want To Logout ?");
    		log.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 35));
    		log.setLayoutX(200);
    		log.setLayoutY(200);
    		
    		Button yes=new Button("Yes");
    		yes.setTextFill(Color.GHOSTWHITE);
    		yes.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 25));
    		yes.setStyle("-fx-background-color: forestgreen;");
    		yes.setPrefWidth(140);
    		yes.setLayoutX(320);
    		yes.setLayoutY(300);
    		
    		Button no=new Button("No");
    		no.setTextFill(Color.GHOSTWHITE);
    		no.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 25));
    		no.setStyle("-fx-background-color: darkred;");
    		no.setPrefWidth(140);
    		no.setLayoutX(500);
    		no.setLayoutY(300);
    		
    		yes.setOnMouseEntered(new EventHandler() 
    	    {
    	    	@Override
    			public void handle(Event me) {
    	            window.setCursor(Cursor.HAND); //Change cursor to hand
    			}
    	    });
    	    yes.setOnMouseExited(new EventHandler() 
    	    {
    	    	@Override
    	        public void handle(Event me) {
    	    		window.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
    	        }
    	    });
    		yes.setOnAction(new EventHandler<ActionEvent>()
    		{
				@Override
				public void handle(ActionEvent arg0)
				{
					try 
					{
						MainPage(primaryStage);
						
					} 
					catch (FileNotFoundException e) 
					{
						e.printStackTrace();
					}
				}
    			
    		});
    		
    		no.setOnMouseEntered(new EventHandler() 
    	    {
    	    	@Override
    			public void handle(Event me) {
    	            window.setCursor(Cursor.HAND); //Change cursor to hand
    			}
    	    });
    	    no.setOnMouseExited(new EventHandler() 
    	    {
    	    	@Override
    	        public void handle(Event me) {
    	    		window.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
    	        }
    	    });
    		no.setOnAction(new EventHandler<ActionEvent>()
		    {
    			@Override
				public void handle(ActionEvent arg0)
				{
					return;
				}	
		    });

    		window.getChildren().add(log);
    		window.getChildren().add(yes);
    		window.getChildren().add(no);
    		masterpane.getChildren().add(window);
		}
		catch (IllegalArgumentException e1)
		{
			System.out.print("");
		}
	}
	
	/**
	 * Serialize room booked list.
	 *
	 * @param room_list the room list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void serialize_RoomBookedList(List<Room> room_list) throws IOException
	{
        ObjectOutputStream outFile=null;
        try
        {
        	outFile=new ObjectOutputStream(new FileOutputStream("./src/RoomBookedList"));
        	outFile.writeObject(room_list);
        }
        finally
        {
        	outFile.close();
        }
	}
	
	/**
	 * Deserialize room booked list.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static List<Room> deserialize_RoomBookedList() throws IOException, ClassNotFoundException  //deserialize
	{
        ObjectInputStream inFile=null;
        try
        {
        	inFile=new ObjectInputStream(new FileInputStream("./src/RoomBookedList"));
        	List<Room> list=new ArrayList<Room>();
        	list=(List<Room>) inFile.readObject();
        	return list;
        }
        finally
        {
        	inFile.close();
        	
        }
	}
	
	/**
	 * Serialize room req.
	 *
	 * @param room_list the room list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void serialize_RoomReq(List<Room> room_list) throws IOException
	{
        ObjectOutputStream outFile=null;
        try
        {
        	outFile=new ObjectOutputStream(new FileOutputStream("./src/RoomRequestList"));
        	outFile.writeObject(room_list);
        }
        finally
        {
        	outFile.close();
        }
	}
	
	/**
	 * Deserialize room req.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static List<Room> deserialize_RoomReq() throws IOException, ClassNotFoundException  //deserialize
	{
        ObjectInputStream inFile=null;
        File f=new File("./src/RoomRequestList");
        try
        {
        	if (!f.exists())
        	{
        		return new ArrayList<Room>();
        	}
        	inFile=new ObjectInputStream(new FileInputStream("./src/RoomRequestList"));
        	List<Room> list=new ArrayList<Room>();
        	list=(List<Room>) inFile.readObject();
        	return list;
        }
        finally
        {
        	inFile.close();
        	
        }
	}
}

/**
 * The Class Profile.
 */
class Profile implements Serializable
{
	
	/** The name. */
	private String name, email_id, password, user_type, program, branch, roll_no;
	
	/**
	 * Sets the name.
	 *
	 * @param _name the new name
	 */
	public void setname(String _name)
	{
		name=_name;
	}
	
	/**
	 * Sets the email.
	 *
	 * @param _email the new email
	 */
	public void setemail(String _email)
	{
		email_id=_email;
	}
	
	/**
	 * Sets the password.
	 *
	 * @param pwd the new password
	 */
	public void setpassword(String pwd)
	{
		password=pwd;
	}
	
	/**
	 * Sets the usertype.
	 *
	 * @param type the new usertype
	 */
	public void setusertype(String type)
	{
		user_type=type;
	}
	
	/**
	 * Sets the program.
	 *
	 * @param prog the new program
	 */
	public void setprogram(String prog)
	{
		program=prog;
	}
	
	/**
	 * Sets the branch.
	 *
	 * @param b the new branch
	 */
	public void setbranch(String b)
	{
		branch=b;
	}
	
	/**
	 * Sets the rollno.
	 *
	 * @param roll the new rollno
	 */
	public void setrollno(String roll)
	{
		roll_no=roll;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getname()
	{
		return name;
	}
	
	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getemail()
	{
		return email_id;
	}
	
	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getpassword()
	{
		return password;
	}
	
	/**
	 * Gets the usertype.
	 *
	 * @return the usertype
	 */
	public String getusertype()
	{
		return user_type;
	}
	
	/**
	 * Gets the program.
	 *
	 * @return the program
	 */
	public String getprogram()
	{
		return program;
	}
	
	/**
	 * Gets the branch.
	 *
	 * @return the branch
	 */
	public String getbranch()
	{
		return branch;
	}
	
	/**
	 * Gets the rollno.
	 *
	 * @return the rollno
	 */
	public String getrollno()
	{
		return roll_no;
	}
}

/**
 * The Class Admin.
 */
class Admin extends User
{
	
	/** The profile. */
	private Profile profile;
	
	/** The list. */
	private transient List<Room> list;
	
	/** The loggedout. */
	private transient int loggedout;
	
	/**
	 * Instantiates a new admin.
	 */
	public Admin()
	{
		profile=new Profile();
		list=new ArrayList<Room>();
	}
    
    /**
     * Start.
     *
     * @param primaryStage the primary stage
     * @param admin_list the admin list
     * @throws Exception the exception
     */
    @SuppressWarnings("unchecked")
	public void start(final Stage primaryStage, final List<Admin> admin_list) throws Exception
    {
    	loggedout=-1;
    	final Button prof=new Button("My Profile");
		prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		prof.setPrefWidth(248);
		prof.setLayoutX(0);
		prof.setLayoutY(230);
		final ImageView imageView1 = new ImageView(new Image(new FileInputStream("Images/profile1.png")));
		imageView1.setFitHeight(30);
		imageView1.setFitWidth(30);
		prof.setGraphic(imageView1);

		final Button book=new Button("Book Room");
		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		book.setPrefWidth(248);
		book.setLayoutX(0);
		book.setLayoutY(290);
		final ImageView imageView2 = new ImageView(new Image(new FileInputStream("Images/book.png")));
		imageView2.setFitHeight(20);
		imageView2.setFitWidth(20);
		book.setGraphic(imageView2);
		
		final Button check=new Button("Room Availability");
		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		check.setPrefWidth(248);
		check.setLayoutX(0);
		check.setLayoutY(350);
		final ImageView imageView3 = new ImageView(new Image(new FileInputStream("Images/check1.png")));
		imageView3.setFitHeight(20);
		imageView3.setFitWidth(20);
		check.setGraphic(imageView3);
		
		final Button cancel=new Button("Cancel Booking");
		cancel.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		cancel.setPrefWidth(248);
		cancel.setLayoutX(0);
		cancel.setLayoutY(410);
		final ImageView imageView4 = new ImageView(new Image(new FileInputStream("Images/cancel.png")));
		imageView4.setFitHeight(20);
		imageView4.setFitWidth(20);
		cancel.setGraphic(imageView4);
		
		final Button request=new Button("View Requests");
		request.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		request.setPrefWidth(248);
		request.setLayoutX(0);
		request.setLayoutY(470);
		final ImageView imageView5 = new ImageView(new Image(new FileInputStream("Images/request1.png")));
		imageView5.setFitHeight(30);
		imageView5.setFitWidth(30);
		request.setGraphic(imageView5);
		
		final Button logout=new Button("Logout");
		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		logout.setPrefWidth(248);
		logout.setLayoutX(0);
		logout.setLayoutY(530);
		final ImageView imageView6 = new ImageView(new Image(new FileInputStream("Images/logout1.png")));
		imageView6.setFitHeight(20);
		imageView6.setFitWidth(20);
		logout.setGraphic(imageView6);
		
		final ImageView imageView = new ImageView(new Image(new FileInputStream("Images/img.jpg")));
    	imageView.setFitHeight(60);
    	imageView.setFitWidth(60);
    	imageView.setLayoutX(20);
    	imageView.setLayoutY(50);
    	
    	Label user=new Label(profile.getname());
    	user.setTextFill(Color.GHOSTWHITE);
    	user.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 17));
    	user.setLayoutX(100);
    	user.setLayoutY(60);
    	
    	Label type=new Label("Admin");
    	type.setTextFill(Color.GHOSTWHITE);
    	type.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
    	type.setLayoutX(100);
    	type.setLayoutY(85);
    	
		final Pane window=new Pane();
    	window.setStyle("-fx-background-color : lightgray;");
    	window.setLayoutX(250);
    	window.setLayoutY(70);
    	window.setPrefSize(1060, 675);
    	//window.getChildren().add(frame);

    	Label welcome=new Label("Welcome");
    	welcome.setTextFill(Color.GHOSTWHITE);
    	welcome.setFont(Font.font("Abyssinica SIL",FontWeight.BOLD,40));
    	welcome.setLayoutX(20);
    	welcome.setLayoutY(15);
    	
    	Pane hPane=new Pane();
    	hPane.setStyle("-fx-background-color : lightseagreen;");
    	hPane.setLayoutX(250);
    	hPane.setLayoutY(0);
    	hPane.setPrefSize(1060, 70);
    	hPane.getChildren().add(welcome);
    	
    	final Pane verticalPane=new Pane();
    	verticalPane.setStyle("-fx-background-color : black;");
    	verticalPane.setLayoutX(0);
    	verticalPane.setLayoutY(0);
    	verticalPane.setPrefSize(250, 745);
    	verticalPane.getChildren().add(imageView);
    	verticalPane.getChildren().add(user);
    	verticalPane.getChildren().add(type);
    	verticalPane.getChildren().add(prof);
    	verticalPane.getChildren().add(book);
    	verticalPane.getChildren().add(check);
    	verticalPane.getChildren().add(cancel);
    	verticalPane.getChildren().add(request);
    	verticalPane.getChildren().add(logout);
    	
    	final Pane masterpane=new Pane();
    	masterpane.getChildren().add(verticalPane);
    	masterpane.getChildren().add(hPane);
    	
    	prof.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            verticalPane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    prof.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	    		verticalPane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    prof.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		try
	    		{
	    			prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: darkblue;");
		    		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		cancel.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		request.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		        	
		    		GridPane grid=new GridPane();
		    		grid.setHgap(300);
		    		grid.setVgap(50);
		    		Label name=new Label("Name");
		    		name.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 30));
		    		grid.add(name, 0, 0);
		    		
		    		TextField uName=new TextField();
		    		uName.setText(getProfile().getname());
		    		uName.setEditable(false);
		    		grid.add(uName, 1, 0, 2, 1);
		    		
		    		Label pass=new Label("Password");
		    		pass.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 30));
		    		grid.add(pass, 0, 1);
		    		
		    		TextField uPass=new TextField();
		    		uPass.setText(getProfile().getpassword());
		    		uPass.setEditable(false);
		    		grid.add(uPass, 1, 1, 2, 1);
		    		
		    		Label type=new Label("User Type");
		    		type.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 30));
		    		grid.add(type, 0, 2);
		    		
		    		TextField uType=new TextField();
		    		uType.setText(getProfile().getusertype());
		    		uType.setEditable(false);
		    		grid.add(uType, 1, 2, 2, 1);
		    		
		    		Label email=new Label("Email ID");
		    		email.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 30));
		    		grid.add(email, 0, 3);
		    		
		    		TextField uEmail=new TextField();
		    		uEmail.setText(getProfile().getemail());
		    		uEmail.setEditable(false);
		    		grid.add(uEmail, 1, 3, 2, 1);
		    		
		    		final Pane frame=new Pane();
		    		frame.setStyle("-fx-background-color: darkgray;");
		    		frame.setLayoutX(20);
		        	frame.setLayoutY(20);
		        	frame.setPrefSize(1010, 635);
		        	grid.setLayoutX(80);
		        	grid.setLayoutY(40);
		        	frame.getChildren().add(grid);
		    		window.getChildren().add(frame);
		        	masterpane.getChildren().add(window);
	    		}
	    		catch (IllegalArgumentException e1)
	    		{
	    			System.out.print("");
	    		}
	   		}
	    });
	    
		book.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            verticalPane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    book.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	    		verticalPane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    book.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		
	    			prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: darkblue;");
		    		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		cancel.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		request.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		
		    		book_room(window, masterpane, "Admin");
	    	}
	    });
	    
	    check.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            verticalPane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    check.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	    		verticalPane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    check.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		
	    			prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: darkblue;");
		    		cancel.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		request.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
	   		
		    		avail_room(window, masterpane);
	   		}
	    });
	    
	    cancel.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            verticalPane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    cancel.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	    		verticalPane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    cancel.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		try
	    		{
		    		prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		cancel.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: darkblue;");
		    		request.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
	    		    
		    		final TableView<MyInfo> table=new TableView<MyInfo>();
		    		table.setLayoutX(20);
		    		table.setLayoutY(20);
		    		table.setPrefSize(960, 565);
		    		table.setEditable(true);
		    		table.setVisible(true);
		    		
					try 
					{
						list = deserialize_RoomBookedList();
					} 
					catch (ClassNotFoundException | IOException e1)
					{
						e1.printStackTrace();
					}
					
					final List<MyInfo> data=new ArrayList<MyInfo>();
					
					for (int i=0; i<list.size(); i++)
					{
						if (list.get(i).getdate()!=null)
							data.add(new MyInfo(list.get(i).getRoom(), list.get(i).getcourse(), list.get(i).getDay()+" "+list.get(i).getdate()+" "+list.get(i).gettime(), list.get(i).getUser()));
						else
							data.add(new MyInfo(list.get(i).getRoom(), list.get(i).getcourse(), list.get(i).getDay()+" "+list.get(i).gettime(), "As Per TimeTable"));
					}
					final ObservableList<MyInfo> data1 =FXCollections.observableArrayList(data);
					
					TableColumn room=new TableColumn("Room");
			        room.setMinWidth(100);
			        room.setCellValueFactory(new PropertyValueFactory<MyInfo, String>("name"));
			        table.getColumns().add(room);
			        
			        TableColumn purp=new TableColumn("Purpose");
			        purp.setMinWidth(196);
			        purp.setCellValueFactory(new PropertyValueFactory<MyInfo, String>("purpose"));
			        table.getColumns().add(purp);
			        
			        TableColumn time=new TableColumn("Time Slot");
			        time.setMinWidth(196);
			        time.setCellValueFactory(new PropertyValueFactory<MyInfo, String>("time"));
			        table.getColumns().add(time);
			        
			        TableColumn bookby=new TableColumn("Booked By");
			        bookby.setMinWidth(196);
			        bookby.setCellValueFactory(new PropertyValueFactory<MyInfo, String>("book"));
			        table.getColumns().add(bookby);
			        
			        TableColumn c2 = new TableColumn("Want To cancel (Tick)");
			        c2.setCellValueFactory(new PropertyValueFactory<MyInfo, Boolean>("checked"));
			        c2.setCellFactory(CheckBoxTableCell.forTableColumn(c2));
			        
			        c2.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>()
			        {
			        	@Override
			            public ObservableValue<Boolean> call(Integer param)
			            {
			        		if (data.get(param).getBook().equals("As Per TimeTable"))
			        			data.get(param).setChecked(false);
			        		return data.get(param).checkedProperty();
			            }
			        }));
			        c2.setEditable(true);
			        c2.setMinWidth(250);
			        table.getColumns().add(c2);
			        
			        final Button can=new Button("Cancel Selected");
			        can.setTextFill(Color.BLACK);
			        can.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 25));
			        can.setStyle("-fx-background-color: linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%),linear-gradient(#020b02, #3a3a3a),linear-gradient(#b9b9b9 0%, #c2c2c2 20%, #afafaf 80%, #c8c8c8 100%),linear-gradient(#f5f5f5 0%, #dbdbdb 50%, #cacaca 51%, #d7d7d7 100%);");
			        can.setLayoutX(370);
			        can.setLayoutY(590);
			        
			        final DropShadow shadow=new DropShadow();
					
					can.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() 
				    {
						@Override
				        public void handle(MouseEvent e) 
				    	{
				    		can.setEffect(shadow);
				        }
					});
					can.addEventHandler(MouseEvent.MOUSE_EXITED,new EventHandler<MouseEvent>() 
					{
						@Override
				    	public void handle(MouseEvent e) 
				    	{
				    		can.setEffect(null);
				    	}
					});
					can.setOnMouseEntered(new EventHandler() 
				    {
				    	@Override
						public void handle(Event me) {
				            window.setCursor(Cursor.HAND); //Change cursor to hand
						}
				    });
				    can.setOnMouseExited(new EventHandler() 
				    {
				    	@Override
				        public void handle(Event me) {
				    		window.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
				        }
				    });
				    table.setItems(data1);
		        	
				    can.setOnAction(new EventHandler<ActionEvent>()
				    {
				    	@Override
				    	public void handle(ActionEvent e)
				    	{
				    		for (int i=0; i<data.size(); i++)
				    		{
				    			if (data.get(i).isChecked()==true)
				    			{
				    				list.remove(i);
				    			}
				    		}
						    data.clear();
						    for (int i=0; i<list.size(); i++)
							{
								if (list.get(i).getdate()!=null)
									data.add(new MyInfo(list.get(i).getRoom(), list.get(i).getcourse(), list.get(i).getDay()+" "+list.get(i).getdate()+" "+list.get(i).gettime(), list.get(i).getUser()));
								else
									data.add(new MyInfo(list.get(i).getRoom(), list.get(i).getcourse(), list.get(i).getDay()+" "+list.get(i).gettime(), "As Per TimeTable"));
							}
						    final ObservableList<MyInfo> data2=FXCollections.observableArrayList(data);
						    table.getItems().clear();
						    table.setItems(data2);
						    try 
						    {
								serialize_RoomBookedList(list);
							} 
						    catch (IOException e1) 
						    {
		 						e1.printStackTrace();
							}
				    	}
				    });
				    

		    		final Pane frame=new Pane();
		    		frame.setStyle("-fx-background-color: darkgray;");
		    		frame.setLayoutX(20);
		        	frame.setLayoutY(20);
		        	frame.setPrefSize(1020, 635);
		        	frame.getChildren().add(can);
		        	frame.getChildren().add(table);
		    		window.getChildren().add(frame);
		        	masterpane.getChildren().add(window);
	    		}
	    		catch (IllegalArgumentException e1)
	    		{
	    			System.out.print("");
	    		}
	   		}
	    });
	    
	    request.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            verticalPane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    request.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	    		verticalPane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    request.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		try
	    		{
	    			prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		cancel.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		request.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: darkblue;");
		    		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
	    		    
		    		final TableView<MyInfo> table=new TableView<MyInfo>();
		    		table.setLayoutX(20);
		    		table.setLayoutY(20);
		    		table.setPrefSize(960, 565);
		    		table.setEditable(true);
		    		table.setVisible(true);
		    		
					try 
					{
						room_req=deserialize_RoomReq();
						list=deserialize_RoomBookedList();
					} 
					catch (ClassNotFoundException | IOException e1)
					{
						e1.printStackTrace();
					}
					
					final List<MyInfo> data=new ArrayList<MyInfo>();
					
					if (room_req!=null) {
					for (int i=0; i<room_req.size(); i++)
					{
						data.add(new MyInfo(room_req.get(i).getRoom(), room_req.get(i).getcourse(), room_req.get(i).getDay()+" "+room_req.get(i).getdate()+" "+room_req.get(i).gettime(), room_req.get(i).getUser()));
					}}
					final ObservableList<MyInfo> data1 =FXCollections.observableArrayList(data);
					
					TableColumn room=new TableColumn("Room");
			        room.setMinWidth(100);
			        room.setCellValueFactory(new PropertyValueFactory<MyInfo, String>("name"));
			        table.getColumns().add(room);
			        
			        TableColumn purp=new TableColumn("Purpose");
			        purp.setMinWidth(196);
			        purp.setCellValueFactory(new PropertyValueFactory<MyInfo, String>("purpose"));
			        table.getColumns().add(purp);
			        
			        TableColumn time=new TableColumn("Time Slot");
			        time.setMinWidth(196);
			        time.setCellValueFactory(new PropertyValueFactory<MyInfo, String>("time"));
			        table.getColumns().add(time);
			        
			        TableColumn bookby=new TableColumn("Requested By");
			        bookby.setMinWidth(196);
			        bookby.setCellValueFactory(new PropertyValueFactory<MyInfo, String>("book"));
			        table.getColumns().add(bookby);
			        
			        TableColumn c2 = new TableColumn("Want To cancel (Tick)");
			        c2.setCellValueFactory(new PropertyValueFactory<MyInfo, Boolean>("checked"));
			        c2.setCellFactory(CheckBoxTableCell.forTableColumn(c2));
			        
			        c2.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>()
			        {
			        	@Override
			            public ObservableValue<Boolean> call(Integer param)
			            {
			        		return data.get(param).checkedProperty();
			            }
			        }));
			        c2.setEditable(true);
			        c2.setMinWidth(250);
			        table.getColumns().add(c2);
			        
			        final Button req=new Button("Accept Selected Request");
			        req.setTextFill(Color.BLACK);
			        req.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 25));
			        req.setStyle("-fx-background-color: linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%),linear-gradient(#020b02, #3a3a3a),linear-gradient(#b9b9b9 0%, #c2c2c2 20%, #afafaf 80%, #c8c8c8 100%),linear-gradient(#f5f5f5 0%, #dbdbdb 50%, #cacaca 51%, #d7d7d7 100%);");
			        req.setLayoutX(370);
			        req.setLayoutY(590);
			        
			        final DropShadow shadow=new DropShadow();
					
					req.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() 
				    {
						@Override
				        public void handle(MouseEvent e) 
				    	{
				    		req.setEffect(shadow);
				        }
					});
					req.addEventHandler(MouseEvent.MOUSE_EXITED,new EventHandler<MouseEvent>() 
					{
						@Override
				    	public void handle(MouseEvent e) 
				    	{
				    		req.setEffect(null);
				    	}
					});
					req.setOnMouseEntered(new EventHandler() 
				    {
				    	@Override
						public void handle(Event me) {
				            window.setCursor(Cursor.HAND); //Change cursor to hand
						}
				    });
				    req.setOnMouseExited(new EventHandler() 
				    {
				    	@Override
				        public void handle(Event me) {
				    		window.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
				        }
				    });
				    table.setItems(data1);
				    
				    req.setOnAction(new EventHandler<ActionEvent>()
				    {
				    	@Override
				    	public void handle(ActionEvent e)
				    	{
				    		for (int i=0; i<data.size(); i++)
				    		{
				    			if (data.get(i).isChecked()==true)
				    			{
				    				Room room=new Room();
				    				room.setcourse(room_req.get(i).getcourse());
			    					room.setday(room_req.get(i).getDay());
			    					room.setdate(room_req.get(i).getdate());
			    					room.settime(room_req.get(i).gettime());
			    					room.setroom(room_req.get(i).getRoom());
			    					room.setaudience(room_req.get(i).getAudience());
			    					room.setuser(room_req.get(i).getUser());
			    					list.add(room);
			    					
				    				room_req.remove(i);
				    			}
				    		}
						    data.clear();
						    for (int i=0; i<room_req.size(); i++)
							{
								data.add(new MyInfo(room_req.get(i).getRoom(), room_req.get(i).getcourse(), room_req.get(i).getDay()+" "+room_req.get(i).getdate()+" "+room_req.get(i).gettime(), room_req.get(i).getUser()));
							}
						    final ObservableList<MyInfo> data2=FXCollections.observableArrayList(data);
						    table.getItems().clear();
						    table.setItems(data2);
						    try 
						    {
								serialize_RoomReq(room_req);
								serialize_RoomBookedList(list);
							} 
						    catch (IOException e1) 
						    {
		 						e1.printStackTrace();
							}
				    	}
				    });
				    for (int i=0; i<data.size(); i++)
				    {
				    	String pattern1="yyyyMMdd";
	    				final DateTimeFormatter dateFormatter1 = DateTimeFormatter.ofPattern(pattern1);
	    				LocalDate date=LocalDate.now();
	    				int today=Integer.parseInt(date.format(dateFormatter1));
	    				
	    				String[] roomdate=data.get(i).getTime().split(" ");
	    				String rdate=roomdate[1].trim();
	    				String[] d=rdate.split("-");
	    				String rd=(d[2]+d[1]+d[0]).trim();
	    				
	    				int reqdate=Integer.parseInt(rd);
	    				if (today>(reqdate+5))
	    					room_req.remove(i);
	    				
	    				data.clear();
					    for (int j=0; j<room_req.size(); j++)
						{
							data.add(new MyInfo(room_req.get(j).getRoom(), room_req.get(j).getcourse(), room_req.get(j).getDay()+" "+room_req.get(j).getdate()+" "+room_req.get(j).gettime(), room_req.get(j).getUser()));
						}
					    final ObservableList<MyInfo> data2=FXCollections.observableArrayList(data);
					    table.getItems().clear();
					    table.setItems(data2);
					    try 
					    {
							serialize_RoomReq(room_req);
							serialize_RoomBookedList(list);
						} 
					    catch (IOException e1) 
					    {
	 						e1.printStackTrace();
						}
				    }
				    
		    		final Pane frame=new Pane();
		    		frame.setStyle("-fx-background-color: darkgray;");
		    		frame.setLayoutX(20);
		        	frame.setLayoutY(20);
		        	frame.setPrefSize(1020, 635);
		        	frame.getChildren().add(req);
		        	frame.getChildren().add(table);
		    		window.getChildren().add(frame);
		        	masterpane.getChildren().add(window);
	    		}
	    		catch (IllegalArgumentException e1)
	    		{
	    			System.out.print("");
	    		}
	   		}
	    });
	    
	    logout.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            verticalPane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    logout.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	    		verticalPane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    logout.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		
		    		window.getChildren().clear();
	    			prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		cancel.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		request.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: darkblue;");
	    		    
		    		logout(primaryStage, window, masterpane);
		    		loggedout=0;
		    		if (loggedout==0)
		    		{
					    for (Admin ad : admin_list)
					    {
					    	if (ad.getProfile().getemail().equals(getProfile().getemail()) && ad.getProfile().getpassword().equals(getProfile().getpassword()))
					    	{
					    		admin_list.remove(ad);
					    		break;
					    	}
					    }
		    		    try 
		    		    {
							update(admin_list);
						} 
		    		    catch (IOException e1) 
		    		    {
							e1.printStackTrace();
						}
		    		}
	   		}
	    });
	    
		final Scene scene=new Scene(masterpane);
		primaryStage.setTitle("ClassRoom Booking Portal | IIIT-D | Admin | Welcome "+profile.getname());
		primaryStage.setScene(scene);
		primaryStage.show();
		prof.fire();
	}
    
    /**
     * Sets the profile.
     *
     * @param name the name
     * @param email the email
     * @param password the password
     * @param usertype the usertype
     */
    public void set_Profile(String name, String email, String password, String usertype)
    {
    	profile.setname(name);
    	profile.setemail(email);
    	profile.setpassword(password);
    	profile.setusertype(usertype);
    }
	
	/**
	 * Gets the profile.
	 *
	 * @return the profile
	 */
	public Profile getProfile()
	{
		return profile;
	}
	
	/**
	 * Update.
	 *
	 * @param admin_list the admin list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void update(List<Admin> admin_list) throws IOException
	{
		admin_list.add(this);
		serialize_Adminlist(admin_list);
	}
	
	/**
	 * Serialize adminlist.
	 *
	 * @param adminlist the adminlist
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void serialize_Adminlist(List<Admin> adminlist) throws IOException
	{
        ObjectOutputStream outFile=null;
        try
        {
        	outFile=new ObjectOutputStream(new FileOutputStream("./src/AdminList"));
        	outFile.writeObject(adminlist);
        }
        finally
        {
        	outFile.close();
        }
	}
	
	/**
	 * The Class MyInfo.
	 */
	public static class MyInfo
    {
		
		/** The rname. */
		private SimpleStringProperty rname, purp, time, book;
		
		/** The checked. */
		private SimpleBooleanProperty checked;
		
    	/**
	     * Instantiates a new my info.
	     *
	     * @param name the name
	     * @param p the p
	     * @param t the t
	     * @param b the b
	     */
	    MyInfo(String name, String p, String t, String b)
    	{
    		rname=new SimpleStringProperty(name);
    		purp=new SimpleStringProperty(p);
    		time=new SimpleStringProperty(t);
    		book=new SimpleStringProperty(b);
    		checked = new SimpleBooleanProperty(false);
    	}
    	
	    /**
	     * Gets the name.
	     *
	     * @return the name
	     */
	    public String getName()
    	{
    		return rname.get();
    	}
    	
	    /**
	     * Gets the purpose.
	     *
	     * @return the purpose
	     */
	    public String getPurpose()
    	{
    		return purp.get();
    	}
    	
	    /**
	     * Gets the time.
	     *
	     * @return the time
	     */
	    public String getTime()
    	{
    		return time.get();
    	}
    	
	    /**
	     * Gets the book.
	     *
	     * @return the book
	     */
	    public String getBook()
    	{
    		return book.get();
    	}
    	
	    /**
	     * Checks if is checked.
	     *
	     * @return true, if is checked
	     */
	    public boolean isChecked() 
    	{
    	      return checked.get();
        }
    	
	    /**
	     * Checked property.
	     *
	     * @return the boolean property
	     */
	    public BooleanProperty checkedProperty() 
   	    {
    		return checked;
   	    }
        
        /**
         * Sets the checked.
         *
         * @param checked the new checked
         */
        public void setChecked(boolean checked)
        {
          this.checked.set(checked);
        }
    }
}

/**
 * The Class Faculty.
 */
class Faculty extends User
{
	
	/** The profile. */
	private Profile profile;
	
	/** The list. */
	private transient List<Room> list;
	
	/** The courses. */
	private List<Course> courses;
	
	/** The loggedout. */
	private transient int loggedout;
	
	/**
	 * Instantiates a new faculty.
	 */
	public Faculty()
	{
		profile=new Profile();
		list=new ArrayList<Room>();
		courses=new ArrayList<Course>();
	}
	
	/**
	 * Start.
	 *
	 * @param primaryStage the primary stage
	 * @param faculty_list the faculty list
	 * @throws Exception the exception
	 */
	public void start(final Stage primaryStage, final List<Faculty> faculty_list) throws Exception
    {
		loggedout-=-1;
		final Button prof=new Button("My Profile");
		prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		prof.setPrefWidth(248);
		prof.setLayoutX(0);
		prof.setLayoutY(230);
		final ImageView imageView1 = new ImageView(new Image(new FileInputStream("Images/profile1.png")));
		imageView1.setFitHeight(30);
		imageView1.setFitWidth(30);
		prof.setGraphic(imageView1);

		final Button book=new Button("Book Room");
		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		book.setPrefWidth(248);
		book.setLayoutX(0);
		book.setLayoutY(290);
		final ImageView imageView2 = new ImageView(new Image(new FileInputStream("Images/book.png")));
		imageView2.setFitHeight(20);
		imageView2.setFitWidth(20);
		book.setGraphic(imageView2);
		
		final Button check=new Button("Room Availability");
		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		check.setPrefWidth(248);
		check.setLayoutX(0);
		check.setLayoutY(350);
		final ImageView imageView3 = new ImageView(new Image(new FileInputStream("Images/check1.png")));
		imageView3.setFitHeight(20);
		imageView3.setFitWidth(20);
		check.setGraphic(imageView3);
		
		final Button cancel=new Button("Cancel Booking");
		cancel.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		cancel.setPrefWidth(248);
		cancel.setLayoutX(0);
		cancel.setLayoutY(410);
		final ImageView imageView4 = new ImageView(new Image(new FileInputStream("Images/cancel.png")));
		imageView4.setFitHeight(20);
		imageView4.setFitWidth(20);
		cancel.setGraphic(imageView4);
		
		final Button course=new Button("My Courses");
		course.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		course.setPrefWidth(248);
		course.setLayoutX(0);
		course.setLayoutY(470);
		final ImageView imageView5 = new ImageView(new Image(new FileInputStream("Images/course.png")));
		imageView5.setFitHeight(30);
		imageView5.setFitWidth(30);
		course.setGraphic(imageView5);
		
		final Button logout=new Button("Logout");
		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		logout.setPrefWidth(248);
		logout.setLayoutX(0);
		logout.setLayoutY(530);
		final ImageView imageView6 = new ImageView(new Image(new FileInputStream("Images/logout1.png")));
		imageView6.setFitHeight(20);
		imageView6.setFitWidth(20);
		logout.setGraphic(imageView6);
		
		final ImageView imageView = new ImageView(new Image(new FileInputStream("Images/event.png")));
    	imageView.setFitHeight(60);
    	imageView.setFitWidth(60);
    	imageView.setLayoutX(20);
    	imageView.setLayoutY(50);
    	
    	Label user=new Label(profile.getname());
    	user.setTextFill(Color.GHOSTWHITE);
    	user.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 17));
    	user.setLayoutX(100);
    	user.setLayoutY(60);
    	
    	Label type=new Label("Faculty");
    	type.setTextFill(Color.GHOSTWHITE);
    	type.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
    	type.setLayoutX(100);
    	type.setLayoutY(85);
    	
		final Pane window=new Pane();
    	window.setStyle("-fx-background-color : lightgray;");
    	window.setLayoutX(250);
    	window.setLayoutY(70);
    	window.setPrefSize(1060, 675);
    	//window.getChildren().add(frame);

    	Label welcome=new Label("Welcome");
    	welcome.setTextFill(Color.GHOSTWHITE);
    	welcome.setFont(Font.font("Abyssinica SIL",FontWeight.BOLD,40));
    	welcome.setLayoutX(20);
    	welcome.setLayoutY(15);
    	
    	Pane hPane=new Pane();
    	hPane.setStyle("-fx-background-color : lightseagreen;");
    	hPane.setLayoutX(250);
    	hPane.setLayoutY(0);
    	hPane.setPrefSize(1060, 70);
    	hPane.getChildren().add(welcome);
    	
    	final Pane verticalPane=new Pane();
    	verticalPane.setStyle("-fx-background-color : black;");
    	verticalPane.setLayoutX(0);
    	verticalPane.setLayoutY(0);
    	verticalPane.setPrefSize(250, 745);
    	verticalPane.getChildren().add(imageView);
    	verticalPane.getChildren().add(user);
    	verticalPane.getChildren().add(type);
    	verticalPane.getChildren().add(prof);
    	verticalPane.getChildren().add(book);
    	verticalPane.getChildren().add(check);
    	verticalPane.getChildren().add(cancel);
    	verticalPane.getChildren().add(course);
    	verticalPane.getChildren().add(logout);
    	
    	final Pane masterpane=new Pane();
    	masterpane.getChildren().add(verticalPane);
    	masterpane.getChildren().add(hPane);
    	
    	prof.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            verticalPane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    prof.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	    		verticalPane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    prof.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		try
	    		{
	    			prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: darkblue;");
		    		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		cancel.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		course.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		        	
		    		GridPane grid=new GridPane();
		    		grid.setHgap(300);
		    		grid.setVgap(50);
		    		Label name=new Label("Name");
		    		name.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 30));
		    		grid.add(name, 0, 0);
		    		
		    		TextField uName=new TextField();
		    		uName.setText(getProfile().getname());
		    		uName.setEditable(false);
		    		grid.add(uName, 1, 0, 2, 1);
		    		
		    		Label pass=new Label("Password");
		    		pass.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 30));
		    		grid.add(pass, 0, 1);
		    		
		    		TextField uPass=new TextField();
		    		uPass.setText(getProfile().getpassword());
		    		uPass.setEditable(false);
		    		grid.add(uPass, 1, 1, 2, 1);
		    		
		    		Label type=new Label("User Type");
		    		type.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 30));
		    		grid.add(type, 0, 2);
		    		
		    		TextField uType=new TextField();
		    		uType.setText(getProfile().getusertype());
		    		uType.setEditable(false);
		    		grid.add(uType, 1, 2, 2, 1);
		    		
		    		Label email=new Label("Email ID");
		    		email.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 30));
		    		grid.add(email, 0, 3);
		    		
		    		TextField uEmail=new TextField();
		    		uEmail.setText(getProfile().getemail());
		    		uEmail.setEditable(false);
		    		grid.add(uEmail, 1, 3, 2, 1);
		    		
		    		final Pane frame=new Pane();
		    		frame.setStyle("-fx-background-color: darkgray;");
		    		frame.setLayoutX(20);
		        	frame.setLayoutY(20);
		        	frame.setPrefSize(1010, 635);
		        	grid.setLayoutX(80);
		        	grid.setLayoutY(40);
		        	frame.getChildren().add(grid);
		    		window.getChildren().add(frame);
		        	masterpane.getChildren().add(window);
	    		}
	    		catch (IllegalArgumentException e1)
	    		{
	    			System.out.print("");
	    		}
	   		}
	    });
	    
		book.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            verticalPane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    book.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	    		verticalPane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    book.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		
	    			prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: darkblue;");
		    		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		cancel.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		course.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		
		    		book_room(window, masterpane, "Faculty");
	    	}
	    });
	    
	    check.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            verticalPane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    check.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	    		verticalPane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    check.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		
	    			prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: darkblue;");
		    		cancel.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		course.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
	   		
		    		avail_room(window, masterpane);
	   		}
	    });
	    
	    cancel.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            verticalPane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    cancel.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	    		verticalPane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    cancel.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		try
	    		{
		    		prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		cancel.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: darkblue;");
		    		course.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
	    		    
		    		final TableView<MyInfo> table=new TableView<MyInfo>();
		    		table.setLayoutX(20);
		    		table.setLayoutY(20);
		    		table.setPrefSize(960, 565);
		    		table.setEditable(true);
		    		table.setVisible(true);
					try 
					{
						list = deserialize_RoomBookedList();
					} catch (ClassNotFoundException | IOException e1)
					{
						e1.printStackTrace();
					}
					
					final List<MyInfo> data=new ArrayList<MyInfo>();
					
					for (int i=0; i<list.size(); i++)
					{
						if (list.get(i).getdate()!=null)
							data.add(new MyInfo(list.get(i).getRoom(), list.get(i).getcourse(), list.get(i).getDay()+" "+list.get(i).getdate()+" "+list.get(i).gettime(), list.get(i).getUser()));
						else
							data.add(new MyInfo(list.get(i).getRoom(), list.get(i).getcourse(), list.get(i).getDay()+" "+list.get(i).gettime(), "As Per TimeTable"));
					}
					final ObservableList<MyInfo> data1 =FXCollections.observableArrayList(data);
					
					TableColumn room=new TableColumn("Room");
			        room.setMinWidth(100);
			        room.setCellValueFactory(new PropertyValueFactory<MyInfo, String>("name"));
			        table.getColumns().add(room);
			        
			        TableColumn purp=new TableColumn("Purpose");
			        purp.setMinWidth(196);
			        purp.setCellValueFactory(new PropertyValueFactory<MyInfo, String>("purpose"));
			        table.getColumns().add(purp);
			        
			        TableColumn time=new TableColumn("Time Slot");
			        time.setMinWidth(196);
			        time.setCellValueFactory(new PropertyValueFactory<MyInfo, String>("time"));
			        table.getColumns().add(time);
			        
			        TableColumn bookby=new TableColumn("Booked By");
			        bookby.setMinWidth(196);
			        bookby.setCellValueFactory(new PropertyValueFactory<MyInfo, String>("book"));
			        table.getColumns().add(bookby);
			        
			        TableColumn c2 = new TableColumn("Want To cancel (Tick)");
			        c2.setCellValueFactory(new PropertyValueFactory<MyInfo, Boolean>("checked"));
			        c2.setCellFactory(CheckBoxTableCell.forTableColumn(c2));
			        
			        c2.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>()
			        {
			        	@Override
			            public ObservableValue<Boolean> call(Integer param)
			            {
			        		if (data.get(param).getBook().equals("As Per TimeTable"))
			        			data.get(param).setChecked(false);
			        		return data.get(param).checkedProperty();
			            }
			        }));
			        c2.setEditable(true);
			        c2.setMinWidth(250);
			        table.getColumns().add(c2);
			        
			        final Button can=new Button("Cancel Selected");
			        can.setTextFill(Color.BLACK);
			        can.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 25));
			        can.setStyle("-fx-background-color: linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%),linear-gradient(#020b02, #3a3a3a),linear-gradient(#b9b9b9 0%, #c2c2c2 20%, #afafaf 80%, #c8c8c8 100%),linear-gradient(#f5f5f5 0%, #dbdbdb 50%, #cacaca 51%, #d7d7d7 100%);");
			        can.setLayoutX(370);
			        can.setLayoutY(590);
			        
			        final DropShadow shadow=new DropShadow();
					
					can.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() 
				    {
						@Override
				        public void handle(MouseEvent e) 
				    	{
				    		can.setEffect(shadow);
				        }
					});
					can.addEventHandler(MouseEvent.MOUSE_EXITED,new EventHandler<MouseEvent>() 
					{
						@Override
				    	public void handle(MouseEvent e) 
				    	{
				    		can.setEffect(null);
				    	}
					});
					can.setOnMouseEntered(new EventHandler() 
				    {
				    	@Override
						public void handle(Event me) {
				            window.setCursor(Cursor.HAND); //Change cursor to hand
						}
				    });
				    can.setOnMouseExited(new EventHandler() 
				    {
				    	@Override
				        public void handle(Event me) {
				    		window.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
				        }
				    });
				    table.setItems(data1);
		        	
				    can.setOnAction(new EventHandler<ActionEvent>()
				    {
				    	@Override
				    	public void handle(ActionEvent e)
				    	{
				    		for (int i=0; i<data.size(); i++)
				    		{
				    			if (data.get(i).isChecked()==true)
				    			{
				    				list.remove(i);
				    			}
				    		}
						    data.clear();
						    for (int i=0; i<list.size(); i++)
							{
								if (list.get(i).getdate()!=null)
									data.add(new MyInfo(list.get(i).getRoom(), list.get(i).getcourse(), list.get(i).getDay()+" "+list.get(i).getdate()+" "+list.get(i).gettime(), list.get(i).getUser()));
								else
									data.add(new MyInfo(list.get(i).getRoom(), list.get(i).getcourse(), list.get(i).getDay()+" "+list.get(i).gettime(), "As Per TimeTable"));
							}
						    final ObservableList<MyInfo> data2=FXCollections.observableArrayList(data);
						    table.getItems().clear();
						    table.setItems(data2);
				    	}
				    });
				    try 
				    {
						serialize_RoomBookedList(list);
					} 
				    catch (IOException e1) 
				    {
 						e1.printStackTrace();
					}

		    		final Pane frame=new Pane();
		    		frame.setStyle("-fx-background-color: darkgray;");
		    		frame.setLayoutX(20);
		        	frame.setLayoutY(20);
		        	frame.setPrefSize(1020, 635);
		        	frame.getChildren().add(can);
		        	frame.getChildren().add(table);
		    		window.getChildren().add(frame);
		        	masterpane.getChildren().add(window);
	    		}
	    		catch (IllegalArgumentException e1)
	    		{
	    			System.out.print("");
	    		}
	   		}
	    });
	    
	    course.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            verticalPane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    course.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	    		verticalPane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    course.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		try
	    		{
	    			prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		cancel.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		course.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: darkblue;");
		    		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
	    		    
		    		final TableView<MyCourse> table=new TableView<MyCourse>();
		    		table.setLayoutX(20);
		    		table.setLayoutY(20);
		    		table.setPrefSize(960, 565);
		    		table.setEditable(true);
		    		table.setVisible(true);

		    		try 
		    		{
						courses=course1.deserialize();
					} 
		    		catch (ClassNotFoundException | IOException e1) 
		    		{
						e1.printStackTrace();
					}
					final List<MyCourse> data=new ArrayList<MyCourse>();
					
					for (int i=0; i<courses.size(); i++)
					{
						if (courses.get(i).getInstructor().equals(getProfile().getname()))
						{
							String time_slot="", rooms="";
							for (int j=0; j<courses.get(i).getRoomList().size(); j++)
							{
								time_slot+="Day: "+courses.get(i).getRoomList().get(j).getDay()+" Time Slot: "+courses.get(i).getRoomList().get(j).gettime()+"\n";
								rooms+=courses.get(i).getRoomList().get(j).getRoom()+"\n";
							}
							data.add(new MyCourse(courses.get(i).getcourse(), time_slot, rooms));
						}
					}
					final ObservableList<MyCourse> data1 =FXCollections.observableArrayList(data);
					
					TableColumn room=new TableColumn("Course");
			        room.setMinWidth(200);
			        room.setCellValueFactory(new PropertyValueFactory<MyCourse, String>("name"));
			        table.getColumns().add(room);
			        
			        TableColumn purp=new TableColumn("Time Slots");
			        purp.setMinWidth(400);
			        purp.setCellValueFactory(new PropertyValueFactory<MyCourse, String>("time"));
			        table.getColumns().add(purp);
			        
			        TableColumn time=new TableColumn("Rooms");
			        time.setMinWidth(400);
			        time.setCellValueFactory(new PropertyValueFactory<MyCourse, String>("room"));
			        table.getColumns().add(time);
			        
			        table.setItems(data1);
			        
			        final Pane frame=new Pane();
		    		frame.setStyle("-fx-background-color: darkgray;");
		    		frame.setLayoutX(20);
		        	frame.setLayoutY(20);
		        	frame.setPrefSize(1020, 635);
		        	frame.getChildren().add(table);
		    		window.getChildren().add(frame);
		        	masterpane.getChildren().add(window);
	    		}
	    		catch (IllegalArgumentException e1)
	    		{
	    			System.out.print("");
	    		}
	   		}
	    });
	    
	    logout.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            verticalPane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    logout.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	    		verticalPane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    logout.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		
		    		window.getChildren().clear();
	    			prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		cancel.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		course.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: darkblue;");
	    		    
		    		logout(primaryStage, window, masterpane);
		    		loggedout=0;
		    		if (loggedout==0)
		    	    {
		    			for (Faculty fac : faculty_list)
					    {
					    	if (fac.getProfile().getemail().equals(getProfile().getemail()) && fac.getProfile().getpassword().equals(getProfile().getpassword()))
					    	{
					    		faculty_list.remove(fac);
					    		break;
					    	}
					    }
		    		    try 
		    		    {
							update(faculty_list);
						} 
		    		    catch (IOException e1) 
		    		    {
							e1.printStackTrace();
						}
		    	    }
				    
	   		}
	    });
	    
		final Scene scene=new Scene(masterpane);
		primaryStage.setTitle("ClassRoom Booking Portal | IIIT-D | Faculty | Welcome "+profile.getname());
		primaryStage.setScene(scene);
		primaryStage.show();
		prof.fire();
	}
	
	/**
	 * Sets the profile.
	 *
	 * @param name the name
	 * @param email the email
	 * @param password the password
	 * @param usertype the usertype
	 */
	public void set_Profile(String name, String email, String password, String usertype)
    {
    	profile.setname(name);
    	profile.setemail(email);
    	profile.setpassword(password);
    	profile.setusertype(usertype);
    }
	
	/**
	 * Gets the profile.
	 *
	 * @return the profile
	 */
	public Profile getProfile()
	{
		return profile;
	}
	
	/**
	 * Update.
	 *
	 * @param faculty_list the faculty list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void update(List<Faculty> faculty_list) throws IOException
	{
		faculty_list.add(this);
		serialize_Facultylist(faculty_list);
	}
	
	/**
	 * Serialize facultylist.
	 *
	 * @param facultylist the facultylist
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void serialize_Facultylist(List<Faculty> facultylist) throws IOException
	{
        ObjectOutputStream outFile=null;
        try
        {
        	outFile=new ObjectOutputStream(new FileOutputStream("./src/FacultyList"));
        	outFile.writeObject(facultylist);
        }
        finally
        {
        	outFile.close();
        }
	}
	
	/**
	 * The Class MyInfo.
	 */
	public static class MyInfo
    {
		
		/** The rname. */
		private SimpleStringProperty rname, purp, time, book;
		
		/** The checked. */
		private SimpleBooleanProperty checked;
		
    	/**
	     * Instantiates a new my info.
	     *
	     * @param name the name
	     * @param p the p
	     * @param t the t
	     * @param b the b
	     */
	    MyInfo(String name, String p, String t, String b)
    	{
    		rname=new SimpleStringProperty(name);
    		purp=new SimpleStringProperty(p);
    		time=new SimpleStringProperty(t);
    		book=new SimpleStringProperty(b);
    		checked = new SimpleBooleanProperty(false);
    	}
    	
	    /**
	     * Gets the name.
	     *
	     * @return the name
	     */
	    public String getName()
    	{
    		return rname.get();
    	}
    	
	    /**
	     * Gets the purpose.
	     *
	     * @return the purpose
	     */
	    public String getPurpose()
    	{
    		return purp.get();
    	}
    	
	    /**
	     * Gets the time.
	     *
	     * @return the time
	     */
	    public String getTime()
    	{
    		return time.get();
    	}
    	
	    /**
	     * Gets the book.
	     *
	     * @return the book
	     */
	    public String getBook()
    	{
    		return book.get();
    	}
    	
	    /**
	     * Checks if is checked.
	     *
	     * @return true, if is checked
	     */
	    public boolean isChecked() 
    	{
    	      return checked.get();
        }
    	
	    /**
	     * Checked property.
	     *
	     * @return the boolean property
	     */
	    public BooleanProperty checkedProperty() 
   	    {
    		return checked;
   	    }
        
        /**
         * Sets the checked.
         *
         * @param checked the new checked
         */
        public void setChecked(boolean checked)
        {
          this.checked.set(checked);
        }
    }
	
	/**
	 * The Class MyCourse.
	 */
	public static class MyCourse
    {
		
		/** The name. */
		private SimpleStringProperty name, time, room;
		
    	/**
	     * Instantiates a new my course.
	     *
	     * @param cname the cname
	     * @param p the p
	     * @param t the t
	     */
	    MyCourse(String cname, String p, String t)
    	{
    		name=new SimpleStringProperty(cname);
    		time=new SimpleStringProperty(p);
    		room=new SimpleStringProperty(t);
    	}
    	
	    /**
	     * Gets the name.
	     *
	     * @return the name
	     */
	    public String getName()
    	{
    		return name.get();
    	}
    	
	    /**
	     * Gets the time.
	     *
	     * @return the time
	     */
	    public String getTime()
    	{
    		return time.get();
    	}
    	
	    /**
	     * Gets the room.
	     *
	     * @return the room
	     */
	    public String getRoom()
    	{
    		return room.get();
    	}
    }
}

/**
 * The Class Student.
 */
class Student extends User
{
	
	/** The profile. */
	private Profile profile;
	
	/** The list. */
	private transient List<Room> list;
	
	/** The courses. */
	private transient List<Course> courses;
	
	/** The mycourses. */
	public List<Course> mycourses, my_courses;
	
	/** The loggedout. */
	private transient int loggedout;
	
	/**
	 * Instantiates a new student.
	 */
	public Student()
	{
		profile=new Profile();
		list=new ArrayList<Room>();
		courses=new ArrayList<Course>();
		try 
		{
			courses=course1.deserialize();
		} 
		catch (ClassNotFoundException | IOException e1) 
		{
			e1.printStackTrace();
		}
		mycourses=new ArrayList<Course>();
		for (Course c : courses)
		{
			if (c.gettype().equals("Mandatory"))
				mycourses.add(c);
		}
	}
	
	/**
	 * Start.
	 *
	 * @param primaryStage the primary stage
	 * @param student_list the student list
	 * @throws Exception the exception
	 */
	public void start(final Stage primaryStage, final List<Student> student_list) throws Exception
    {
		loggedout=-1;
		my_courses=new ArrayList<Course>();
		final Button prof=new Button("My Profile");
		prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		prof.setPrefWidth(248);
		prof.setLayoutX(0);
		prof.setLayoutY(230);
		final ImageView imageView1 = new ImageView(new Image(new FileInputStream("Images/profile1.png")));
		imageView1.setFitHeight(30);
		imageView1.setFitWidth(30);
		prof.setGraphic(imageView1);

		final Button book=new Button("Send Request");
		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		book.setPrefWidth(248);
		book.setLayoutX(0);
		book.setLayoutY(290);
		final ImageView imageView2 = new ImageView(new Image(new FileInputStream("Images/book.png")));
		imageView2.setFitHeight(20);
		imageView2.setFitWidth(20);
		book.setGraphic(imageView2);
		
		final Button check=new Button("Room Availability");
		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		check.setPrefWidth(248);
		check.setLayoutX(0);
		check.setLayoutY(350);
		final ImageView imageView3 = new ImageView(new Image(new FileInputStream("Images/check1.png")));
		imageView3.setFitHeight(20);
		imageView3.setFitWidth(20);
		check.setGraphic(imageView3);
		
		final Button course=new Button("My Courses");
		course.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		course.setPrefWidth(248);
		course.setLayoutX(0);
		course.setLayoutY(410);
		final ImageView imageView5 = new ImageView(new Image(new FileInputStream("Images/course.png")));
		imageView5.setFitHeight(30);
		imageView5.setFitWidth(30);
		course.setGraphic(imageView5);
		
		final Button search=new Button("Search Courses");
		search.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		search.setPrefWidth(248);
		search.setLayoutX(0);
		search.setLayoutY(470);
		final ImageView imageView6 = new ImageView(new Image(new FileInputStream("Images/search.png")));
		imageView6.setFitHeight(30);
		imageView6.setFitWidth(30);
		search.setGraphic(imageView6);
		
		final Button events=new Button("Events");
		events.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		events.setPrefWidth(248);
		events.setLayoutX(0);
		events.setLayoutY(530);
		final ImageView imageView7 = new ImageView(new Image(new FileInputStream("Images/event.png")));
		imageView7.setFitHeight(30);
		imageView7.setFitWidth(30);
		events.setGraphic(imageView7);
		
		final Button logout=new Button("Logout");
		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		logout.setPrefWidth(248);
		logout.setLayoutX(0);
		logout.setLayoutY(590);
		final ImageView imageView8 = new ImageView(new Image(new FileInputStream("Images/logout1.png")));
		imageView8.setFitHeight(20);
		imageView8.setFitWidth(20);
		logout.setGraphic(imageView8);
		
		final ImageView imageView = new ImageView(new Image(new FileInputStream("Images/event.png")));
    	imageView.setFitHeight(60);
    	imageView.setFitWidth(60);
    	imageView.setLayoutX(20);
    	imageView.setLayoutY(50);
    	
    	Label user=new Label(profile.getname());
    	user.setTextFill(Color.GHOSTWHITE);
    	user.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 17));
    	user.setLayoutX(100);
    	user.setLayoutY(60);
    	
    	Label type=new Label("Student");
    	type.setTextFill(Color.GHOSTWHITE);
    	type.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
    	type.setLayoutX(100);
    	type.setLayoutY(85);
    	
		final Pane window=new Pane();
    	window.setStyle("-fx-background-color : lightgray;");
    	window.setLayoutX(250);
    	window.setLayoutY(70);
    	window.setPrefSize(1060, 675);
    	//window.getChildren().add(frame);

    	Label welcome=new Label("Welcome");
    	welcome.setTextFill(Color.GHOSTWHITE);
    	welcome.setFont(Font.font("Abyssinica SIL",FontWeight.BOLD,40));
    	welcome.setLayoutX(20);
    	welcome.setLayoutY(15);
    	
    	Pane hPane=new Pane();
    	hPane.setStyle("-fx-background-color : lightseagreen;");
    	hPane.setLayoutX(250);
    	hPane.setLayoutY(0);
    	hPane.setPrefSize(1060, 70);
    	hPane.getChildren().add(welcome);
    	
    	final Pane verticalPane=new Pane();
    	verticalPane.setStyle("-fx-background-color : black;");
    	verticalPane.setLayoutX(0);
    	verticalPane.setLayoutY(0);
    	verticalPane.setPrefSize(250, 745);
    	verticalPane.getChildren().add(imageView);
    	verticalPane.getChildren().add(user);
    	verticalPane.getChildren().add(type);
    	verticalPane.getChildren().add(prof);
    	verticalPane.getChildren().add(book);
    	verticalPane.getChildren().add(check);
    	verticalPane.getChildren().add(course);
    	verticalPane.getChildren().add(search);
    	verticalPane.getChildren().add(events);
    	verticalPane.getChildren().add(logout);
    	
    	final Pane masterpane=new Pane();
    	masterpane.getChildren().add(verticalPane);
    	masterpane.getChildren().add(hPane);
    	
    	prof.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            verticalPane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    prof.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	    		verticalPane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    prof.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		try
	    		{
	    			prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: darkblue;");
		    		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		course.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		search.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		events.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		        	
		    		GridPane grid=new GridPane();
		    		grid.setHgap(300);
		    		grid.setVgap(50);
		    		Label name=new Label("Name");
		    		name.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 30));
		    		grid.add(name, 0, 0);
		    		
		    		TextField uName=new TextField();
		    		uName.setText(getProfile().getname());
		    		uName.setEditable(false);
		    		grid.add(uName, 1, 0, 2, 1);
		    		
		    		Label pass=new Label("Password");
		    		pass.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 30));
		    		grid.add(pass, 0, 1);
		    		
		    		TextField uPass=new TextField();
		    		uPass.setText(getProfile().getpassword());
		    		uPass.setEditable(false);
		    		grid.add(uPass, 1, 1, 2, 1);
		    		
		    		Label type=new Label("User Type");
		    		type.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 30));
		    		grid.add(type, 0, 2);
		    		
		    		TextField uType=new TextField();
		    		uType.setText(getProfile().getusertype());
		    		uType.setEditable(false);
		    		grid.add(uType, 1, 2, 2, 1);
		    		
		    		Label email=new Label("Email ID");
		    		email.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 30));
		    		grid.add(email, 0, 3);
		    		
		    		TextField uEmail=new TextField();
		    		uEmail.setText(getProfile().getemail());
		    		uEmail.setEditable(false);
		    		grid.add(uEmail, 1, 3, 2, 1);
		    		
		    		Label prog=new Label("Program");
		    		prog.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 30));
		    		grid.add(prog, 0, 4);
		    		
		    		TextField uProg=new TextField();
		    		uProg.setText(getProfile().getprogram());
		    		uProg.setEditable(true);
		    		grid.add(uProg, 1, 4, 2, 1);
		    		
		    		Label b=new Label("Branch");
		    		b.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 30));
		    		grid.add(b, 0, 5);
		    		
		    		TextField uB=new TextField();
		    		uB.setText(getProfile().getbranch());
		    		uB.setEditable(true);
		    		grid.add(uB, 1, 5, 2, 1);
		    		
		    		Label roll=new Label("Roll No.");
		    		roll.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 30));
		    		grid.add(roll, 0, 6);
		    		
		    		TextField uRoll=new TextField();
		    		uRoll.setText(getProfile().getrollno());
		    		uRoll.setEditable(true);
		    		grid.add(uRoll, 1, 6, 2, 1);
		    		
		    		final Pane frame=new Pane();
		    		frame.setStyle("-fx-background-color: darkgray;");
		    		frame.setLayoutX(20);
		        	frame.setLayoutY(20);
		        	frame.setPrefSize(1010, 635);
		        	grid.setLayoutX(80);
		        	grid.setLayoutY(40);
		        	frame.getChildren().add(grid);
		    		window.getChildren().add(frame);
		        	masterpane.getChildren().add(window);
	    		}
	    		catch (IllegalArgumentException e1)
	    		{
	    			System.out.print("");
	    		}
	   		}
	    });
	    
		book.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            verticalPane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    book.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	    		verticalPane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    book.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		
	    			prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: darkblue;");
		    		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		course.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		search.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		events.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		
		    		book_room(window, masterpane, "Student-"+getProfile().getname()+"("+getProfile().getrollno()+")");
	    	}
	    });
	    
	    check.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            verticalPane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    check.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	    		verticalPane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    check.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		
	    			prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: darkblue;");
		    		course.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
	   		
		    		avail_room(window, masterpane);
	   		}
	    });
	    
	    course.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            verticalPane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    course.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	    		verticalPane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    course.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		try
	    		{
	    			prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		course.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: darkblue;");
		    		search.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		events.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
	    		    
		    		final TableView<MyCourse> table=new TableView<MyCourse>();
		    		table.setLayoutX(20);
		    		table.setLayoutY(20);
		    		table.setPrefSize(960, 565);
		    		table.setEditable(true);
		    		table.setVisible(true);
		    		
					final List<MyCourse> data=new ArrayList<MyCourse>();
					if (my_courses.size()!=0)
						mycourses=my_courses;
					for (int i=0; i<mycourses.size(); i++)
					{
						String time_slot="", rooms="";
						for (int j=0; j<mycourses.get(i).getRoomList().size(); j++)
						{
							time_slot+="Day: "+mycourses.get(i).getRoomList().get(j).getDay()+" Time Slot: "+mycourses.get(i).getRoomList().get(j).gettime()+"\n";
							rooms+=mycourses.get(i).getRoomList().get(j).getRoom()+"\n";
						}
						data.add(new MyCourse(mycourses.get(i).getcourse(), time_slot, rooms));
					}
					final ObservableList<MyCourse> data1 =FXCollections.observableArrayList(data);
					
					TableColumn room=new TableColumn("Course");
			        room.setMinWidth(200);
			        room.setCellValueFactory(new PropertyValueFactory<MyCourse, String>("name"));
			        table.getColumns().add(room);
			        
			        TableColumn purp=new TableColumn("Time Slots");
			        purp.setMinWidth(400);
			        purp.setCellValueFactory(new PropertyValueFactory<MyCourse, String>("time"));
			        table.getColumns().add(purp);
			        
			        TableColumn time=new TableColumn("Rooms");
			        time.setMinWidth(400);
			        time.setCellValueFactory(new PropertyValueFactory<MyCourse, String>("room"));
			        table.getColumns().add(time);
			        
			        table.setItems(data1);
			        
			        final Pane frame=new Pane();
		    		frame.setStyle("-fx-background-color: darkgray;");
		    		frame.setLayoutX(20);
		        	frame.setLayoutY(20);
		        	frame.setPrefSize(1020, 635);
		        	frame.getChildren().add(table);
		    		window.getChildren().add(frame);
		        	masterpane.getChildren().add(window);
	    		}
	    		catch (IllegalArgumentException e1)
	    		{
	    			System.out.print("");
	    		}
	   		}
	    });
	    
	    search.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            verticalPane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    search.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	    		verticalPane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    search.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		try
	    		{
		    		window.getChildren().clear();
	    			prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		course.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		search.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: darkblue;");
		    		events.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
	    		    
		    		final ImageView imageView = new ImageView(new Image(new FileInputStream("Images/search2.png")));
		        	imageView.setFitHeight(30);
		        	imageView.setFitWidth(30);
		        	imageView.setLayoutX(20);
		        	imageView.setLayoutY(50);
		        	
		    		final TextField sc=new TextField();
		    		sc.setPromptText("Enter Keywords");
		    		sc.setLayoutX(200);
		    		sc.setLayoutY(30);
		    		sc.setPrefSize(500, 45);
		    		sc.setEditable(true);
		    		
		    		final Button s=new Button("Search");
		    		s.setLayoutX(750);
		    		s.setLayoutY(30);
		    		s.setGraphic(imageView);
		    		s.setTextFill(Color.BLACK);
			        s.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 20));
			        s.setStyle("-fx-background-color: linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%),linear-gradient(#020b02, #3a3a3a),linear-gradient(#b9b9b9 0%, #c2c2c2 20%, #afafaf 80%, #c8c8c8 100%),linear-gradient(#f5f5f5 0%, #dbdbdb 50%, #cacaca 51%, #d7d7d7 100%);");
		    		
			        final TableView<Courses> table=new TableView<Courses>();
		    		table.setLayoutX(20);
		    		table.setLayoutY(100);
		    		table.setPrefSize(980, 475);
		    		table.setEditable(true);
		    		table.setVisible(true);
		    		
					try
					{
						courses=course1.deserialize();
					} 
					catch (ClassNotFoundException | IOException e1)
					{
						e1.printStackTrace();
					}
					
					final List<Courses> data=new ArrayList<Courses>();
					
					TableColumn room=new TableColumn("Course");
			        room.setMinWidth(400);
			        room.setCellValueFactory(new PropertyValueFactory<Courses, String>("name"));
			        table.getColumns().add(room);
			        
			        TableColumn purp=new TableColumn("Time Slot");
			        purp.setMinWidth(196);
			        purp.setCellValueFactory(new PropertyValueFactory<Courses, String>("time"));
			        table.getColumns().add(purp);
			        
			        TableColumn time=new TableColumn("Room");
			        time.setMinWidth(80);
			        time.setCellValueFactory(new PropertyValueFactory<Courses, String>("room"));
			        table.getColumns().add(time);
			        
			        TableColumn bookby=new TableColumn("Post Conditions");
			        bookby.setMinWidth(5000);
			        bookby.setCellValueFactory(new PropertyValueFactory<Courses, List<String>>("pC"));
			        table.getColumns().add(bookby);
			        
			        TableColumn c2 = new TableColumn("Want To cancel (Tick)");
			        c2.setCellValueFactory(new PropertyValueFactory<Courses, Boolean>("checked"));
			        c2.setCellFactory(CheckBoxTableCell.forTableColumn(c2));
			        
			        c2.setEditable(true);
			        c2.setMinWidth(200);
			        table.getColumns().add(c2);
			        
			        final Button register=new Button("Register Selected");
			        register.setTextFill(Color.BLACK);
			        register.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 25));
			        register.setStyle("-fx-background-color: linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%),linear-gradient(#020b02, #3a3a3a),linear-gradient(#b9b9b9 0%, #c2c2c2 20%, #afafaf 80%, #c8c8c8 100%),linear-gradient(#f5f5f5 0%, #dbdbdb 50%, #cacaca 51%, #d7d7d7 100%);");
			        register.setLayoutX(370);
			        register.setLayoutY(590);
			        
			        final DropShadow shadow=new DropShadow();
					
					register.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() 
				    {
						@Override
				        public void handle(MouseEvent e) 
				    	{
				    		register.setEffect(shadow);
				        }
					});
					register.addEventHandler(MouseEvent.MOUSE_EXITED,new EventHandler<MouseEvent>() 
					{
						@Override
				    	public void handle(MouseEvent e) 
				    	{
				    		register.setEffect(null);
				    	}
					});
					register.setOnMouseEntered(new EventHandler() 
				    {
				    	@Override
						public void handle(Event me) {
				            window.setCursor(Cursor.HAND); //Change cursor to hand
						}
				    });
					register.setOnMouseExited(new EventHandler() 
				    {
				    	@Override
				        public void handle(Event me) {
				    		window.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
				        }
				    });
		        	
					s.setOnAction(new EventHandler<ActionEvent>()
					{
						@Override
						public void handle(ActionEvent e)
						{
							data.clear();
							ObservableList<Courses> data1 =FXCollections.observableArrayList(data);
							table.setItems(data1);
							String kw=sc.getText();
							int var;
							for (int i=0; i<courses.size(); i++)
							{
								var=-1;
								String time_slot="";
								String rooms="";
								for (int j=0; j<courses.get(i).getPc().size(); j++)
								{
									if (courses.get(i).getPc().get(j).replace(".", "").toLowerCase().contains(kw.toLowerCase()))
									{
										var=i;
										break;
									}
								}
								if (var!=-1)
								{
									for (int k=0; k<courses.get(var).getRoomList().size(); k++)
									{
										time_slot+="Day:"+courses.get(var).getRoomList().get(k).getDay()+" TimeSlot:"+courses.get(var).getRoomList().get(k).gettime()+"\n";
										rooms+=courses.get(var).getRoomList().get(k).getRoom()+"\n";
									}
									data.add(new Courses(courses.get(var).getcourse()+" "+"("+courses.get(var).gettype()+")", time_slot, rooms, courses.get(var).getPc()));
								}
							}
							data1 =FXCollections.observableArrayList(data);
						    table.setItems(data1);
						    
						}
					});
					
			        c2.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>()
			        {
			        	@Override
			            public ObservableValue<Boolean> call(Integer param)
			            {
			        		if (data.get(param).getName().contains(("Mandatory")))
			        			data.get(param).setChecked(false);
			        		return data.get(param).checkedProperty();
			            }
			        }));
			        
			        final ContextMenu regValidator=new ContextMenu();
			        regValidator.hide();
			        regValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;");
			        regValidator.getItems().clear();
			        
				    register.setOnAction(new EventHandler<ActionEvent>()
				    {
				    	@Override
				    	public void handle(ActionEvent e)
				    	{
				    		for (int i=0; i<data.size(); i++)
				    		{
				    			boolean found1=false, found2=false;
				    			String[] time1=data.get(i).getTime().split("\n");
				    			int[] day1=new int[10];
				    			String[] time2=new String[10];
				    			String name=data.get(i).getName().replace("(Elective)", "").trim();
				    			int l=0;
				    			for (l=0; l<time1.length; l++)
				    			{
				    				String[] time3=time1[l].split(" ");
					    			day1[l]=Integer.parseInt(time3[0].substring(time3[0].length()-1));
					    			time2[l]=time3[1].substring(9, time3[1].length()).trim();
				    			}
				    			if (data.get(i).isChecked()==true && data.get(i).getName().contains("Elective"))
				    			{
				    				String[] time=new String[10];
				    				int[] day=new int[10];
				    				for (int j=0; j<mycourses.size(); j++)
				    				{
				    					int k=0;
				    					for (k=0; k<mycourses.get(j).getRoomList().size(); k++)
				    					{
				    						time[k]=mycourses.get(j).getRoomList().get(k).gettime().trim();
				    					    day[k]=mycourses.get(j).getRoomList().get(k).getDay();
				    					}
				    					if (!name.trim().equalsIgnoreCase(mycourses.get(j).getcourse().trim()))
				    					{
				    						for (int m=0; m<l; m++)
				    						{
				    							for (int n=0; n<k; n++)
				    							{
				    								if (day1[m]==day[n] && time[n].trim().equals(time2[m].trim()))
				    								{
				    									found1=true;
					    								regValidator.getItems().clear();
									                    MenuItem m4=new MenuItem("Timings In Conflict");
									                    m4.setStyle("-fx-text-fill: red");
									                    regValidator.getItems().add(m4);
									                    regValidator.show(register, Side.RIGHT, 10, 0);
					    								break;
				    								}
				    							}
				    							if (found1==true)
				    								break;
				    						} 
				    					}
				    					else
				    					{
				    						found1=true;
				    						regValidator.getItems().clear();
						                    MenuItem m4=new MenuItem("Already Registered");
						                    m4.setStyle("-fx-text-fill: red");
						                    regValidator.getItems().add(m4);
						                    regValidator.show(register, Side.RIGHT, 10, 0);
		    								break;
				    					}
				    					if (found1==true)
				    						break;
				    				}
				    				for (int j=0; j<data.size(); j++)
				    				{
				    					if (data.get(j).isChecked()==true && data.get(j).getName().contains("Elective") && !data.get(j).getName().equalsIgnoreCase(data.get(i).getName()))
				    					{
				    						String[] time11=data.get(j).getTime().split("\n");
							    			int[] day11=new int[10];
							    			String[] time21=new String[10];
							    			String name1="";
							    			int l1=0;
							    			for (l1=0; l1<time11.length; l1++)
							    			{
							    				String[] time3=time11[l1].split(" ");
								    			day11[l1]=Integer.parseInt(time3[0].substring(time3[0].length()-1));
								    			time21[l1]=time3[1].substring(9, time3[1].length()).trim();
							    			}
							    			for (int m=0; m<l; m++)
				    						{
				    							for (int n=0; n<l1; n++)
				    							{
				    								if (day1[m]==day11[n] && time21[n].trim().equals(time2[m].trim()))
				    								{
				    									found2=true;
					    								regValidator.getItems().clear();
									                    MenuItem m4=new MenuItem("Timings In Conflict");
									                    m4.setStyle("-fx-text-fill: red");
									                    regValidator.getItems().add(m4);
									                    regValidator.show(register, Side.RIGHT, 10, 0);
					    								break;
				    								}
				    							}
				    							if (found2==true)
				    								break;
				    						}
				    					}
				    					if (found2==true)
				    						break;
				    				}
				    				if (found1==false && found2==false)
					    			{
					    				for (int g=0; g<courses.size(); g++)
						    			{
						    				if (courses.get(g).getcourse().trim().equalsIgnoreCase(name))
						    				{
						    					mycourses.add(courses.get(g));
						    					my_courses=mycourses;
						    					regValidator.getItems().clear();
							                    MenuItem m4=new MenuItem("Registered");
							                    m4.setStyle("-fx-text-fill: ghostwhite");
							                    regValidator.getItems().add(m4);
							                    regValidator.show(register, Side.RIGHT, 10, 0);
						    				}
						    			}
					    			}
				    			}	
				    		}
				    	}
				    });
			        final Pane frame=new Pane();
		    		frame.setStyle("-fx-background-color: darkgray;");
		    		frame.setLayoutX(20);
		        	frame.setLayoutY(20);
		        	frame.setPrefSize(1030, 635);
		        	frame.getChildren().add(sc);
		        	frame.getChildren().add(s);
		        	frame.getChildren().add(register);
		        	frame.getChildren().add(table);
		    		window.getChildren().add(frame);
		        	masterpane.getChildren().add(window);
	    		}
	    		catch (IllegalArgumentException | FileNotFoundException e1)
	    		{
	    			System.out.print("");
	    		}
	   		}
	    });
	    events.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            verticalPane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    events.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	    		verticalPane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    events.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		try 
	    		{
	    			window.getChildren().clear();
	    			prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		course.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		search.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		events.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: darkblue;");
		    		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
	    		    
		    		final TableView<MyInfo> table=new TableView<MyInfo>();
		    		table.setLayoutX(20);
		    		table.setLayoutY(20);
		    		table.setPrefSize(960, 565);
		    		table.setEditable(true);
		    		table.setVisible(true);
		    		
					try 
					{
						list = deserialize_RoomBookedList();
					} 
					catch (ClassNotFoundException | IOException e1)
					{
						e1.printStackTrace();
					}
					
					final List<MyInfo> data=new ArrayList<MyInfo>();
					
					for (int i=0; i<list.size(); i++)
					{
						if (list.get(i).getdate()!=null)
						{
							data.add(new MyInfo(list.get(i).getRoom(), list.get(i).getcourse(), list.get(i).getDay()+" "+list.get(i).getdate()+" "+list.get(i).gettime(), list.get(i).getUser()));
						}
					}
					final ObservableList<MyInfo> data1 =FXCollections.observableArrayList(data);
					
					TableColumn room=new TableColumn("Room");
			        room.setMinWidth(100);
			        room.setCellValueFactory(new PropertyValueFactory<MyInfo, String>("name"));
			        table.getColumns().add(room);
			        
			        TableColumn purp=new TableColumn("Purpose");
			        purp.setMinWidth(196);
			        purp.setCellValueFactory(new PropertyValueFactory<MyInfo, String>("purpose"));
			        table.getColumns().add(purp);
			        
			        TableColumn time=new TableColumn("Time Slot");
			        time.setMinWidth(196);
			        time.setCellValueFactory(new PropertyValueFactory<MyInfo, String>("time"));
			        table.getColumns().add(time);
			        
			        TableColumn bookby=new TableColumn("Booked By");
			        bookby.setMinWidth(196);
			        bookby.setCellValueFactory(new PropertyValueFactory<MyInfo, String>("book"));
			        table.getColumns().add(bookby);
			        
			        table.setItems(data1);
			        
			        final Pane frame=new Pane();
		    		frame.setStyle("-fx-background-color: darkgray;");
		    		frame.setLayoutX(20);
		        	frame.setLayoutY(20);
		        	frame.setPrefSize(1020, 635);
		        	frame.getChildren().add(table);
		    		window.getChildren().add(frame);
		        	masterpane.getChildren().add(window);
	    		}
	    		catch (IllegalArgumentException e1)
	    		{
	    			System.out.print("");
	    		}
	   		}
	    });
	    
	    logout.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            verticalPane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    logout.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	    		verticalPane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    logout.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		
		    		window.getChildren().clear();
	    			prof.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		book.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		check.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		course.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		search.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		events.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: black;");
		    		logout.setStyle("-fx-font: 20 arial; -fx-text-fill: ghostwhite; -fx-background-color: darkblue;");
	    		    
		    		logout(primaryStage, window, masterpane);
		    		loggedout=0;
		    		if (loggedout==0)
		    	    {
		    		    for (Student stu : student_list)
		    		    {
		    		    	if (stu.getProfile().getemail().equals(getProfile().getemail()) && stu.getProfile().getpassword().equals(getProfile().getpassword()))
		    		    	{
		    		    		student_list.remove(stu);
		    		    		break;
		    		    	}
		    		    }
		    		    try 
		    		    {
							update(student_list);
						} 
		    		    catch (IOException e1) 
		    		    {
							e1.printStackTrace();
						}
		    	    }
	   		}
	    });    
		final Scene scene=new Scene(masterpane);
		primaryStage.setTitle("ClassRoom Booking Portal | IIIT-D | Faculty | Welcome "+profile.getname());
		primaryStage.setScene(scene);
		primaryStage.show();
		prof.fire();
	}
	
	/**
	 * Sets the profile.
	 *
	 * @param name the name
	 * @param email the email
	 * @param password the password
	 * @param usertype the usertype
	 * @param prog the prog
	 * @param branch the branch
	 * @param roll the roll
	 */
	public void set_Profile(String name, String email, String password, String usertype, String prog, String branch, String roll)
    {
    	profile.setname(name);
    	profile.setemail(email);
    	profile.setpassword(password);
    	profile.setusertype(usertype);
    	profile.setprogram(prog);
    	profile.setbranch(branch);
    	profile.setrollno(roll);
    }
	
	/**
	 * Gets the profile.
	 *
	 * @return the profile
	 */
	public Profile getProfile()
	{
		return profile;
	}
	
	/**
	 * Update.
	 *
	 * @param student_list the student list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void update(List<Student> student_list) throws IOException
	{
	    mycourses=my_courses;
		student_list.add(this);
		serialize_Studentlist(student_list);
	}
	/**
	 * The Class MyInfo.
	 */
	public static class MyInfo
    {
		
		/** The rname. */
		private SimpleStringProperty rname, purp, time, book;
		
    	/**
	     * Instantiates a new my info.
	     *
	     * @param name the name
	     * @param p the p
	     * @param t the t
	     * @param b the b
	     */
	    MyInfo(String name, String p, String t, String b)
    	{
    		rname=new SimpleStringProperty(name);
    		purp=new SimpleStringProperty(p);
    		time=new SimpleStringProperty(t);
    		book=new SimpleStringProperty(b);
    	}
    	
	    /**
	     * Gets the name.
	     *
	     * @return the name
	     */
	    public String getName()
    	{
    		return rname.get();
    	}
    	
	    /**
	     * Gets the purpose.
	     *
	     * @return the purpose
	     */
	    public String getPurpose()
    	{
    		return purp.get();
    	}
    	
	    /**
	     * Gets the time.
	     *
	     * @return the time
	     */
	    public String getTime()
    	{
    		return time.get();
    	}
    	
	    /**
	     * Gets the book.
	     *
	     * @return the book
	     */
	    public String getBook()
    	{
    		return book.get();
    	}
    }
	/**
	 * The Class Courses.
	 */
	public static class Courses
    {
		
		/** The rname. */
		private SimpleStringProperty rname, time, room;
		
		/** The post. */
		private List<String> post;
		
		/** The checked. */
		private SimpleBooleanProperty checked;
		
    	/**
	     * Instantiates a new courses.
	     *
	     * @param name the name
	     * @param t the t
	     * @param r the r
	     * @param list the list
	     */
	    Courses(String name, String t, String r, List<String> list)
    	{
    		rname=new SimpleStringProperty(name);
    		time=new SimpleStringProperty(t);
    		room=new SimpleStringProperty(r);
    		post=list;
    		checked = new SimpleBooleanProperty(false);
    	}
    	
	    /**
	     * Gets the name.
	     *
	     * @return the name
	     */
	    public String getName()
    	{
    		return rname.get();
    	}
    	
	    /**
	     * Gets the time.
	     *
	     * @return the time
	     */
	    public String getTime()
    	{
    		return time.get();
    	}
    	
	    /**
	     * Gets the room.
	     *
	     * @return the room
	     */
	    public String getRoom()
    	{
    		return room.get();
    	}
    	
	    /**
	     * Gets the pc.
	     *
	     * @return the pc
	     */
	    public List<String> getPC()
    	{
    		return post;
    	}
    	
	    /**
	     * Checks if is checked.
	     *
	     * @return true, if is checked
	     */
	    public boolean isChecked() 
    	{
    	      return checked.get();
        }
    	
	    /**
	     * Checked property.
	     *
	     * @return the boolean property
	     */
	    public BooleanProperty checkedProperty() 
   	    {
    		return checked;
   	    }
        
        /**
         * Sets the checked.
         *
         * @param checked the new checked
         */
        public void setChecked(boolean checked)
        {
          this.checked.set(checked);
        }
    }
	
	/**
	 * The Class MyCourse.
	 */
	public static class MyCourse
    {
		
		/** The name. */
		private SimpleStringProperty name, time, room;
		
    	/**
	     * Instantiates a new my course.
	     *
	     * @param cname the cname
	     * @param p the p
	     * @param t the t
	     */
	    MyCourse(String cname, String p, String t)
    	{
    		name=new SimpleStringProperty(cname);
    		time=new SimpleStringProperty(p);
    		room=new SimpleStringProperty(t);
    	}
    	
	    /**
	     * Gets the name.
	     *
	     * @return the name
	     */
	    public String getName()
    	{
    		return name.get();
    	}
    	
	    /**
	     * Gets the time.
	     *
	     * @return the time
	     */
	    public String getTime()
    	{
    		return time.get();
    	}
    	
	    /**
	     * Gets the room.
	     *
	     * @return the room
	     */
	    public String getRoom()
    	{
    		return room.get();
    	}
    }
	
	/**
	 * Deserialize.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static List<Course> deserialize() throws IOException, ClassNotFoundException
	{
		ObjectInputStream inFile=null;
		try
        {
        	inFile=new ObjectInputStream(new FileInputStream("./src/Monsoon2017_Course_info"));
        	List<Course> list=new ArrayList<Course>();
        	list=(List<Course>) inFile.readObject();
        	return list;
        }
        finally
        {
        	inFile.close();
        }
	}
	
	/**
	 * Serialize studentlist.
	 *
	 * @param studentlist the studentlist
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void serialize_Studentlist(List<Student> studentlist) throws IOException
	{
        ObjectOutputStream outFile=null;
        try
        {
        	outFile=new ObjectOutputStream(new FileOutputStream("./src/StudentList"));
        	outFile.writeObject(studentlist);
        }
        finally
        {
        	outFile.close();
        }
	}
}

/**
 * The Class mainPage.
 */
class mainPage
{
	
	/**
	 * Main page.
	 *
	 * @param primaryStage the primary stage
	 * @throws FileNotFoundException the file not found exception
	 */
	@SuppressWarnings("unchecked")
	public void MainPage(final Stage primaryStage) throws FileNotFoundException
	{
		final ImageView imageView = new ImageView(new Image(new FileInputStream("Images/IIITDeve.jpg")));
	    imageView.setFitHeight(745);
		imageView.setFitWidth(1305);
		
		Text text=new Text();
		text.setText("Welcome To IIITD");
		text.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 47.0));
		text.setFill(Color.FLORALWHITE);
	    text.setX(100);
	    text.setY(300);
	    
	    Text text2=new Text();
	    text2.setText("ClassRoom Booking Portal");
	    text2.setFont(Font.font("Abyssinica SIL", FontWeight.EXTRA_BOLD, 47.0));
		text2.setFill(Color.FLORALWHITE);
	    text2.setX(40);
	    text2.setY(360);
	    
	    final Pane pane=new Pane();
	    pane.getChildren().addAll(text, text2);
	    
	    final DropShadow shadow=new DropShadow();
	    
	    final Button login=new Button();
	    login.setText("Login");
	    login.setStyle("-fx-font: 25 arial; -fx-base: DARKSLATEBLUE; -fx-background-radius: 30; -fx-border-color: DARKSLATEBLUE; -fx-border-radius: 30;");
	    login.setLayoutX(120);
	    login.setLayoutY(440);
	    login.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() 
	    {
	    	@Override
	        public void handle(MouseEvent e) 
	    	{
	    		login.setEffect(shadow);
	        }
	    });
	    login.addEventHandler(MouseEvent.MOUSE_EXITED,new EventHandler<MouseEvent>() 
	    {
	    	@Override
	    	public void handle(MouseEvent e) 
	    	{
	    		login.setEffect(null);
	    	}
	    });
	    pane.getChildren().add(login);
	    
	    Text text3=new Text();
	    text3.setText("Already a member?");
	    text3.setFont(Font.font("Abyssinica SIL", 18.0));
	    text3.setFill(Color.BLACK);
	    text3.setX(90);
	    text3.setY(430);
	    pane.getChildren().add(text3);
	    
	    final Button signup=new Button();
	    signup.setText("Signup");
	    signup.setStyle("-fx-font: 25 arial; -fx-base: silver; -fx-background-radius: 30; -fx-border-color: silver; -fx-border-radius: 30;");
	    signup.setLayoutX(340);
	    signup.setLayoutY(440);
	    signup.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() 
	    {
	    	@Override
	        public void handle(MouseEvent e) 
	    	{
	    		signup.setEffect(shadow);
	        }
	    });
	    signup.addEventHandler(MouseEvent.MOUSE_EXITED,new EventHandler<MouseEvent>() 
	    {
	    	@Override
	    	public void handle(MouseEvent e) 
	    	{
	    		signup.setEffect(null);
	    	}
	    });
	    pane.getChildren().add(signup);
	    
	    Text text4=new Text();
	    text4.setText("Not a registered user yet?");
	    text4.setFont(Font.font("Abyssinica SIL", 18.0));
	    text4.setFill(Color.BLACK);
	    text4.setX(300);
	    text4.setY(430);
	    pane.getChildren().add(text4);
	    
	    
        StackPane layout = new StackPane();
	    layout.getChildren().addAll(imageView, pane);
	    final Scene scene=new Scene(layout);

	    login.setOnMouseEntered(new EventHandler<Event>() 
	    {
	    	@Override
			public void handle(Event me) {
	            pane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    login.setOnMouseExited(new EventHandler<Event>() 
	    {
	    	@Override
	        public void handle(Event me) {
	            pane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    login.setOnAction(new EventHandler<ActionEvent>() 
	    {
			@Override
			public void handle(ActionEvent e) 
			{
				Login login=new Login();
	    		try {
					login.login(primaryStage);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
	    });
	    signup.setOnMouseEntered(new EventHandler<Event>() 
	    {
	    	@Override
			public void handle(Event me) {
	            pane.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    signup.setOnMouseExited(new EventHandler<Event>() 
	    {
	    	@Override
	        public void handle(Event me) {
	            pane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    signup.setOnAction(new EventHandler<ActionEvent>() 
	    {
	    	@Override
	    	public void handle(ActionEvent e) 
	   		{
	    		Signup signup=new Signup();
	    		try 
	    		{
					signup.signup(primaryStage);
				} 
	    		catch (FileNotFoundException e1) 
				{
					e1.printStackTrace();
				}
	    	}
	    });
	    primaryStage.setTitle("ClassRoom Booking Portal | IIIT-D");
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}
}

/**
 * The Class Login.
 */
class Login extends mainPage implements Serializable
{
	
	/** The email id. */
	String email_id, password;
	
	/** The user. */
	User user;	
	
	/**
	 * Login.
	 *
	 * @param primaryStage the primary stage
	 * @throws FileNotFoundException the file not found exception
	 */
	public void login(Stage primaryStage) throws FileNotFoundException
	{
		start(primaryStage);
	}
	
	/**
	 * Start.
	 *
	 * @param primaryStage the primary stage
	 * @throws FileNotFoundException the file not found exception
	 */
	@SuppressWarnings("unchecked")
	public void start(final Stage primaryStage) throws FileNotFoundException
	{
		final ImageView imageView1 = new ImageView(new Image(new FileInputStream("Images/login3.png")));
		imageView1.setFitHeight(150);
		imageView1.setFitWidth(150);
		primaryStage.setTitle("ClassRoom Booking Portal | IIIT-D | Login");
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setPrefSize(500, 150);
		grid.setHgap(30);
		grid.setVgap(20);
		grid.setPadding(new Insets(30,30,30,30));
		
		Label uName=new Label("Email ID:");
		uName.setFont(Font.font("Abyssinica SIL",FontWeight.SEMI_BOLD,30));
		uName.setTextFill(Color.GHOSTWHITE);
		grid.add(uName,0,1);
		final TextField uField=new TextField();
		uField.setPromptText("Name");
		grid.add(uField,1,1);

		Label pass=new Label("Password:");
		pass.setFont(Font.font("Abyssinica SIL",FontWeight.SEMI_BOLD,30));
		pass.setTextFill(Color.GHOSTWHITE);
		grid.add(pass,0,2);
		final PasswordField passField=new PasswordField();
		passField.setPromptText("Password");
		grid.add(passField,1,2);

		final DropShadow shadow=new DropShadow();
		final Button login=new Button("Login");
		login.setStyle("-fx-font: 25 arial; -fx-text-fill: white; -fx-background-color: #a6b5c9,linear-gradient(#303842 0%, #3e5577 20%, #375074 100%),linear-gradient(#768aa5 0%, #849cbb 5%, #5877a2 50%, #486a9a 51%, #4a6c9b 100%);-fx-background-radius: 15; -fx-border-color: silver; -fx-border-radius: 15;");
	    login.setLayoutX(340);
	    login.setLayoutY(440);
		login.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() 
	    {
			@Override
	        public void handle(MouseEvent e) 
	    	{
	    		login.setEffect(shadow);
	        }
	    });
	    login.addEventHandler(MouseEvent.MOUSE_EXITED,new EventHandler<MouseEvent>() 
		{
	    	@Override
	    	public void handle(MouseEvent e) 
	    	{
	    		login.setEffect(null);
	    	}
		});
        HBox sign=new HBox(10);
        sign.setAlignment(Pos.CENTER);
        sign.getChildren().add(login);
        grid.add(sign,1,4);
        
        final ContextMenu emailValidator=new ContextMenu();
        emailValidator.setAutoHide(false);
        emailValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;-fx-padding: 0;");
        
        final ContextMenu passValidator=new ContextMenu();
        passValidator.setAutoHide(false);
        passValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;-fx-padding: 0;");

        login.setOnAction(new EventHandler<ActionEvent>() 
        {
        	@Override
        	public void handle(ActionEvent e) 
        	{
        		email_id=uField.getText(); 
        		if (uField.getText().equals(""))
        		{
        			ImageView imageView;
					try 
					{
						imageView = new ImageView(new Image(new FileInputStream("Images/mark1.png")));
						imageView.setFitHeight(17);
	        			imageView.setFitWidth(20);
	                    emailValidator.getItems().clear();
	                    MenuItem m1=new MenuItem("", imageView);
	                    emailValidator.getItems().add(m1);
	                    emailValidator.show(uField, Side.RIGHT, 10, 0);
					} 
					catch (FileNotFoundException e1) 
					{
						e1.printStackTrace();
					}
        		}
        		password=passField.getText();
        		if (passField.getText().equals(""))
        		{
        			ImageView imageView;
					try
					{
						imageView = new ImageView(new Image(new FileInputStream("Images/mark1.png")));
	        			imageView.setFitHeight(17);
	        			imageView.setFitWidth(20);
	                    passValidator.getItems().clear();
	                    MenuItem m2=new MenuItem("", imageView);
	                    passValidator.getItems().add(m2);
	                    passValidator.show(passField, Side.RIGHT, 10, 0);
					} 
					catch (FileNotFoundException e1) 
					{
						e1.printStackTrace();
					}
        		}
        		if (!uField.getText().equals("") && !email_id.contains("@student.usm.my"))
    			{
    				ImageView imageView1;
					try 
					{
						imageView1 = new ImageView(new Image(new FileInputStream("Images/warn.png")));
	        			imageView1.setFitHeight(15);
	        			imageView1.setFitWidth(20);
	                    emailValidator.getItems().clear();
	                    MenuItem m5=new MenuItem("Incorrect Email ID", imageView1);
	                    m5.setStyle("-fx-text-fill: white;");
	                    emailValidator.getItems().add(m5);
	                    emailValidator.show(uField, Side.BOTTOM, 5, 0);
					} 
					catch (FileNotFoundException e1) 
					{
						e1.printStackTrace();
					}
    			}
        		if (email_id.contains("@student.usm.my") && !passField.getText().equals(""))
        		{
        			try 
    				{
    					user=getUser(email_id, password);
    					if (user==null)
    					{
    						uField.replaceText(0, uField.getText().length(), "");
    						passField.replaceText(0, passField.getText().length(), "");
    						ImageView imageView;
    						try 
    						{
    							imageView = new ImageView(new Image(new FileInputStream("Images/warn.png")));
    		        			imageView.setFitHeight(15);
    		        			imageView.setFitWidth(20);
    		                    passValidator.getItems().clear();
    		                    MenuItem m5=new MenuItem("Invalid Email ID or Password", imageView);
    		                    m5.setStyle("-fx-text-fill: red;");
    		                    passValidator.getItems().add(m5);
    		                    passValidator.show(passField, Side.BOTTOM, 5, 0);
    						} 
    						catch (FileNotFoundException e1) 
    						{
    							e1.printStackTrace();
    						}
    					}
    					else
    					{
    						if (user.getClass().toString().equals("class Admin"))
        					{
        						Admin admin=(Admin) user;
        						List<Admin> admin_list=get_AdminList();
        						admin.start(primaryStage, admin_list);
        						
        					}
        					else if (user.getClass().toString().equals("class Faculty"))
        					{
        						Faculty faculty=(Faculty) user;
        						List<Faculty> faculty_list=get_FacultyList();
        						faculty.start(primaryStage, faculty_list);
        					}
        					else
        					{
        						Student student=(Student) user;
        						List<Student> student_list=get_StudentList();
        						student.start(primaryStage, student_list); 
        					}
    					}
    				} 
    				catch (ClassNotFoundException e1) 
    				{
    					e1.printStackTrace();
    				} 
    				catch (IOException e1) 
    				{
    					e1.printStackTrace();
    				} 
        			catch (Exception e1)
    				{
						e1.printStackTrace();
					}	
        		}
    		}
        });
        uField.focusedProperty().addListener(new ChangeListener<Boolean>() 
        {
        	@Override
            public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
			{
                if (newPropertyValue) 
                {
                    emailValidator.hide();
                }
            }
         });
        passField.focusedProperty().addListener(new ChangeListener<Boolean>() 
        {
        	@Override
            public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
			{
                if (newPropertyValue) 
                {
                    passValidator.hide();
                }
            }
        });
        final Button back=new Button("Back");
		back.setStyle("-fx-font: 25 arial; -fx-background-color: #c3c4c4,linear-gradient(#d6d6d6 50%, white 100%),radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%); -fx-background-radius: 15; -fx-border-color: silver; -fx-border-radius: 15;");
	    back.setLayoutX(340);
	    back.setLayoutY(440);
		back.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() 
	    {
			@Override
	        public void handle(MouseEvent e) 
	    	{
	    		back.setEffect(shadow);
	        }
	    });
	    back.addEventHandler(MouseEvent.MOUSE_EXITED,new EventHandler<MouseEvent>() 
		{
	    	@Override
	    	public void handle(MouseEvent e) 
	    	{
	    		back.setEffect(null);
	    	}
		});
        grid.add(back, 0, 4);
        back.setOnAction(new EventHandler<ActionEvent>()
        {
        	@Override
        	public void handle(ActionEvent e)
        	{
        		try 
        		{
					MainPage(primaryStage);
					emailValidator.hide();
					passValidator.hide();;
				} 
        		catch (FileNotFoundException e1) 
        		{
					e1.printStackTrace();
				}
        	}
        });   
		Pane pane1=new Pane();
		final ImageView imageView3 = new ImageView(new Image(new FileInputStream("Images/login_back.jpg")));
		imageView3.setFitHeight(730);
		imageView3.setFitWidth(1290);
		imageView3.setLayoutX(5);
		imageView3.setLayoutY(5);
		pane1.getChildren().add(imageView3);
		final Pane pane2=new Pane();
		pane2.setLayoutX(350);
		pane2.setLayoutY(220);
		Pane pane3=new Pane();
		pane3.getChildren().add(imageView1);
		pane3.setLayoutX(520);
		pane3.setLayoutY(130);
		grid.setLayoutY(50);
		pane2.getChildren().add(grid);
		pane2.setStyle("-fx-background-color: rgba(0, 100, 150, 0.5); -fx-background-radius: 10;");
		pane1.getChildren().add(pane2);
		pane1.getChildren().add(pane3);
		Pane masterPane=new Pane();
		masterPane.getChildren().addAll(pane1);
        final Scene scene=new Scene(masterPane);
        login.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            pane2.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    login.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	            pane2.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
        back.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            pane2.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    back.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	            pane2.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
        primaryStage.setScene(scene);
        primaryStage.show();
    }
	
	/**
	 * Gets the user.
	 *
	 * @param email the email
	 * @param password the password
	 * @return the user
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private User getUser(String email, String password) throws ClassNotFoundException, IOException
	{
		if (A_deserialize(email, password)!=null)
			return A_deserialize(email, password);
		else if (F_deserialize(email, password)!=null)
			return F_deserialize(email, password);
		else
			return S_deserialize(email, password);
	}
	
	/**
	 * A deserialize.
	 *
	 * @param email the email
	 * @param password the password
	 * @return the admin
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static Admin A_deserialize(String email, String password) throws IOException, ClassNotFoundException
	{
        ObjectInputStream inFile=null;
        try
        {
        	inFile=new ObjectInputStream(new FileInputStream("./src/AdminList"));
        	List<Admin> list=new ArrayList<Admin>();
        	list=(List<Admin>) inFile.readObject();
            for (Admin admin : list)
            {
            	if (admin.getProfile().getemail().equals(email) && admin.getProfile().getpassword().equals(password))
            		return admin;
            }
        }
        finally
        {
        	inFile.close();
        	
        }
        return null;
	}
	
	/**
	 * F deserialize.
	 *
	 * @param email the email
	 * @param password the password
	 * @return the faculty
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static Faculty F_deserialize(String email, String password) throws IOException, ClassNotFoundException
	{
        ObjectInputStream inFile=null;
        try
        {
        	inFile=new ObjectInputStream(new FileInputStream("./src/FacultyList"));
        	List<Faculty> list=new ArrayList<Faculty>();
        	list=(List<Faculty>) inFile.readObject();
            for (Faculty faculty : list)
            {
            	if (faculty.getProfile().getemail().equals(email) && faculty.getProfile().getpassword().equals(password))
            		return faculty;
            }
        }
        finally
        {
        	inFile.close();
        	
        }
        return null;
	}
	
	/**
	 * S deserialize.
	 *
	 * @param email the email
	 * @param password the password
	 * @return the student
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static Student S_deserialize(String email, String password) throws IOException, ClassNotFoundException
	{
        ObjectInputStream inFile=null;
        try
        {
        	inFile=new ObjectInputStream(new FileInputStream("./src/StudentList"));
        	List<Student> list=new ArrayList<Student>();
        	list=(List<Student>) inFile.readObject();
            for (Student student : list)
            {
            	if (student.getProfile().getemail().equals(email) && student.getProfile().getpassword().equals(password))
            		return student;
            }
        }
        finally
        {
        	inFile.close();
        	
        }
        return null;
	}
	
	/**
	 * Serialize adminlist.
	 *
	 * @param adminlist the adminlist
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void serialize_Adminlist(List<Admin> adminlist) throws IOException
	{
        ObjectOutputStream outFile=null;
        try
        {
        	outFile=new ObjectOutputStream(new FileOutputStream("./src/AdminList"));
        	outFile.writeObject(adminlist);
        }
        finally
        {
        	outFile.close();
        }
	}
	
	/**
	 * Serialize facultylist.
	 *
	 * @param facultylist the facultylist
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void serialize_Facultylist(List<Faculty> facultylist) throws IOException
	{
        ObjectOutputStream outFile=null;
        try
        {
        	outFile=new ObjectOutputStream(new FileOutputStream("./src/FacultyList"));
        	outFile.writeObject(facultylist);
        }
        finally
        {
        	outFile.close();
        }
	}
	
	/**
	 * Serialize studentlist.
	 *
	 * @param studentlist the studentlist
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void serialize_Studentlist(List<Student> studentlist) throws IOException
	{
        ObjectOutputStream outFile=null;
        try
        {
        	outFile=new ObjectOutputStream(new FileOutputStream("./src/StudentList"));
        	outFile.writeObject(studentlist);
        }
        finally
        {
        	outFile.close();
        }
	}
	
	/**
	 * Gets the admin list.
	 *
	 * @return the admin list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static List<Admin> get_AdminList() throws IOException, ClassNotFoundException  //deserialize
	{
        ObjectInputStream inFile=null;
        try
        {
        	inFile=new ObjectInputStream(new FileInputStream("./src/AdminList"));
        	List<Admin> list=new ArrayList<Admin>();
        	list=(List<Admin>) inFile.readObject();
        	return list;
        }
        finally
        {
        	inFile.close();
        	
        }
	}
	
	/**
	 * Gets the faculty list.
	 *
	 * @return the faculty list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static List<Faculty> get_FacultyList() throws IOException, ClassNotFoundException  //deserialize
	{
        ObjectInputStream inFile=null;
        try
        {
        	inFile=new ObjectInputStream(new FileInputStream("./src/FacultyList"));
        	List<Faculty> list=new ArrayList<Faculty>();
        	list=(List<Faculty>) inFile.readObject();
        	return list;
        }
        finally
        {
        	inFile.close();
        	
        }
	}
	
	/**
	 * Gets the student list.
	 *
	 * @return the student list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static List<Student> get_StudentList() throws IOException, ClassNotFoundException  //deserialize
	{
        ObjectInputStream inFile=null;
        try
        {
        	inFile=new ObjectInputStream(new FileInputStream("./src/StudentList"));
        	List<Student> list=new ArrayList<Student>();
        	list=(List<Student>) inFile.readObject();
        	return list;
        }
        finally
        {
        	inFile.close();
        	
        }
	}
}

/**
 * The Class Signup.
 */
class Signup extends mainPage implements Serializable
{
	
	/** The name. */
	private String name, roll_no, email_id, password, password1, user_type, prog, branch_name;
	
	/** The admin. */
	private Admin admin;
	
	/** The faculty. */
	private Faculty faculty;
	
	/** The student. */
	private Student student;
	
	/**
	 * Signup.
	 *
	 * @param primaryStage the primary stage
	 * @throws FileNotFoundException the file not found exception
	 */
	public void signup(Stage primaryStage) throws FileNotFoundException 
	{
		start(primaryStage);
	}
	
	/**
	 * Start.
	 *
	 * @param primaryStage the primary stage
	 * @throws FileNotFoundException the file not found exception
	 */
	@SuppressWarnings("unchecked")
	public void start(final Stage primaryStage) throws FileNotFoundException 
	{
		final ImageView imageView1 = new ImageView(new Image(new FileInputStream("Images/signup_icon.png")));
		imageView1.setFitHeight(150);
		imageView1.setFitWidth(150);
		primaryStage.setTitle("ClassRoom Booking Portal | IIIT-D | Register");
		final GridPane grid = new GridPane();
		grid.setPrefSize(700, 0);
		grid.setHgap(60);
		grid.setVgap(20);
		grid.setPadding(new Insets(30,60,20,100));

		Label uName=new Label("Name");
		uName.setFont(Font.font("Abyssinica SIL",FontWeight.SEMI_BOLD,30));
		uName.setTextFill(Color.GHOSTWHITE);
		grid.add(uName,0,1);
		final TextField uField=new TextField();
		uField.setPromptText("Name");
		grid.add(uField,1,1);
		
		Label pass=new Label("Password");
		pass.setFont(Font.font("Abyssinica SIL",FontWeight.SEMI_BOLD,30));
		pass.setTextFill(Color.GHOSTWHITE);
		grid.add(pass,0,2);
		final PasswordField passField=new PasswordField();
		passField.setPromptText("Password");
		grid.add(passField,1,2);
		
		Label cpass=new Label("Confirm Password");
		cpass.setFont(Font.font("Abyssinica SIL",FontWeight.SEMI_BOLD,30));
		cpass.setTextFill(Color.GHOSTWHITE);
		grid.add(cpass,0,3);
		final PasswordField cpassField=new PasswordField();
		cpassField.setPromptText("ConfirmPassword");
		grid.add(cpassField,1,3);
		
		Label type = new Label("Type of User");
		type.setFont(Font.font("Abyssinica SIL",FontWeight.SEMI_BOLD,30));
		type.setTextFill(Color.GHOSTWHITE);
		type.setAlignment(Pos.BOTTOM_LEFT);
		grid.add(type,0,4);
		final ComboBox<String> myComboBox=new ComboBox<String>();
		myComboBox.setEditable(true); 
		myComboBox.setPromptText("Type of User");
		TypeUser(myComboBox);
		HBox typebox=new HBox();
		//typebox.setSpacing(20);
		typebox.getChildren().add(myComboBox);
		grid.add(typebox,1,4);
		
		Label program=new Label("Program");
		program.setFont(Font.font("Abyssinica SIL",FontWeight.SEMI_BOLD,30));
		program.setTextFill(Color.GHOSTWHITE);
		grid.add(program,0,6);
		final ComboBox<String> myComboBox1=new ComboBox<String>();
		myComboBox1.setVisible(true);
		myComboBox1.setPromptText("Program");
		Program(myComboBox1);
		HBox typebox1=new HBox();
		typebox1.getChildren().add(myComboBox1);
		grid.add(typebox1,1,6);
		
		Label branch=new Label("Branch");
		branch.setFont(Font.font("Abyssinica SIL",FontWeight.SEMI_BOLD,30));
		branch.setTextFill(Color.GHOSTWHITE);
		grid.add(branch,0,7);
		final ComboBox<String> myComboBox2=new ComboBox<String>();
		myComboBox2.setVisible(true);
		myComboBox2.setPromptText("Branch");
		Branch(myComboBox2);
		HBox typebox2=new HBox();
		typebox2.getChildren().add(myComboBox2);
		grid.add(typebox2,1,7);
		
		Label roll=new Label("Roll No");
		roll.setFont(Font.font("Abyssinica SIL",FontWeight.SEMI_BOLD,30));
		roll.setTextFill(Color.GHOSTWHITE);
		grid.add(roll,0,8);
		final TextField rField=new TextField();
		rField.setPromptText("Roll No (Only Numeric Text)");
		rField.setVisible(true);
		grid.add(rField,1,8);
		
		Label email=new Label("Email ID");
		email.setFont(Font.font("Abyssinica SIL",FontWeight.SEMI_BOLD,30));
		email.setTextFill(Color.GHOSTWHITE);
		grid.add(email,0,5);
		final TextField eField=new TextField();
		eField.setPromptText("Email ID");
		eField.setOnMouseClicked(new EventHandler<Event>()
		{
			@Override
			public void handle(Event e)
			{
				if (myComboBox.getSelectionModel().getSelectedItem().toString().equals("Student"))
				{
					myComboBox1.setVisible(true);
					myComboBox1.setEditable(true);
					myComboBox2.setVisible(true);
					myComboBox2.setEditable(true);
					rField.setVisible(true);
				}
			}
		});
		grid.add(eField,1,5);
		
		final DropShadow shadow=new DropShadow();
		final Button signin=new Button("Register");
		signin.setStyle("-fx-font: 25 arial; -fx-background-color: linear-gradient(#ffd65b, #e68400),linear-gradient(#ffef84, #f2ba44),linear-gradient(#ffea6a, #efaa22),linear-gradient(#ffe657 0%, #f8c202 50%, #eea10b 100%),linear-gradient(from 0% 0% to 15% 50%, rgba(255,255,255,0.9), rgba(255,255,255,0)); -fx-background-radius: 15; -fx-border-color: silver; -fx-border-radius: 15;");
	    signin.setLayoutX(340);
	    signin.setLayoutY(440);
		signin.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() 
	    {
			@Override
	        public void handle(MouseEvent e) 
	    	{
	    		signin.setEffect(shadow);
	        }
	    });
	    signin.addEventHandler(MouseEvent.MOUSE_EXITED,new EventHandler<MouseEvent>() 
		{
	    	@Override
	    	public void handle(MouseEvent e) 
	    	{
	    		signin.setEffect(null);
	    	}
		});
        HBox sign=new HBox(10);
        sign.setAlignment(Pos.CENTER);
        sign.getChildren().add(signin);
        grid.add(sign,1,9);
        
        final ContextMenu nameValidator=new ContextMenu();
        nameValidator.setAutoHide(false);
        nameValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;-fx-padding: 0;");
        
        final ContextMenu passValidator=new ContextMenu();
        passValidator.setAutoHide(false);
        passValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;-fx-padding: 0;");
        
        final ContextMenu cpassValidator=new ContextMenu();
        cpassValidator.setAutoHide(false);
        cpassValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;-fx-padding: 0;");
        
        final ContextMenu typeValidator=new ContextMenu();
        typeValidator.setAutoHide(false);
        typeValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;-fx-padding: 0;");

        final ContextMenu emailValidator=new ContextMenu();
        emailValidator.setAutoHide(false);
        emailValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;-fx-padding: 0;");
        
        final ContextMenu progValidator=new ContextMenu();
        progValidator.setAutoHide(false);
        progValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;-fx-padding: 0;");
        
        final ContextMenu branchValidator=new ContextMenu();
        branchValidator.setAutoHide(false);
        branchValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;-fx-padding: 0;");
        
        final ContextMenu rollValidator=new ContextMenu();
        rollValidator.setAutoHide(false);
        rollValidator.setStyle("-fx-background-color: transparent;-fx-text-fill: white;-fx-padding: 0;");
        
		signin.setOnAction(new EventHandler<ActionEvent>() 
        {
        	@Override
        	public void handle(ActionEvent e) 
        	{
        		name=uField.getText(); 
        		if (uField.getText().equals(""))
        		{
        			ImageView imageView;
					try 
					{
						imageView = new ImageView(new Image(new FileInputStream("Images/mark1.png")));
						imageView.setFitHeight(17);
	        			imageView.setFitWidth(20);
	                    nameValidator.getItems().clear();
	                    MenuItem m1=new MenuItem("", imageView);
	                    nameValidator.getItems().add(m1);
	                    nameValidator.show(uField, Side.RIGHT, 10, 0);
					} 
					catch (FileNotFoundException e1) 
					{
						e1.printStackTrace();
					}
        			
        		}
        		password=passField.getText();
        		if (passField.getText().equals(""))
        		{
        			ImageView imageView;
					try
					{
						imageView = new ImageView(new Image(new FileInputStream("Images/mark1.png")));
	        			imageView.setFitHeight(17);
	        			imageView.setFitWidth(20);
	                    passValidator.getItems().clear();
	                    MenuItem m2=new MenuItem("", imageView);
	                    passValidator.getItems().add(m2);
	                    passValidator.show(passField, Side.RIGHT, 10, 0);
					} 
					catch (FileNotFoundException e1) 
					{
						e1.printStackTrace();
					}
                    
        		}
        		password1=cpassField.getText();
        		if (cpassField.getText().equals(""))
        		{
        			ImageView imageView;
					try 
					{
						imageView = new ImageView(new Image(new FileInputStream("Images/mark1.png")));
						imageView.setFitHeight(17);
	        			imageView.setFitWidth(20);
	                    cpassValidator.getItems().clear();
	                    MenuItem m3=new MenuItem("", imageView);
	                    cpassValidator.getItems().add(m3);
	                    cpassValidator.show(cpassField, Side.RIGHT, 10, 0);
					} 
					catch (FileNotFoundException e1) 
					{
						e1.printStackTrace();
					}
        		}
                try
                {
                	user_type=myComboBox.getSelectionModel().getSelectedItem().toString();
                }
                catch (NullPointerException ex)
                {
                	ImageView imageView;
					try 
					{
						imageView = new ImageView(new Image(new FileInputStream("Images/mark1.png")));
	            		imageView.setFitHeight(17);
	            		imageView.setFitWidth(20);
	                	typeValidator.getItems().clear();
	                    MenuItem m4=new MenuItem("", imageView);
	                    typeValidator.getItems().add(m4);
	                    typeValidator.show(myComboBox, Side.RIGHT, 10, 0);
					} 
					catch (FileNotFoundException e1)
					{
						e1.printStackTrace();
					}
                }
                email_id=eField.getText();
        		if (eField.getText().equals(""))
        		{
        			ImageView imageView;
					try 
					{
						imageView = new ImageView(new Image(new FileInputStream("Images/mark1.png")));
	        			imageView.setFitHeight(17);
	        			imageView.setFitWidth(20);
	                    emailValidator.getItems().clear();
	                    MenuItem m5=new MenuItem("", imageView);
	                    emailValidator.getItems().add(m5);
	                    emailValidator.show(eField, Side.RIGHT, 10, 0);
					} 
					catch (FileNotFoundException e1) 
					{
						e1.printStackTrace();
					}
        		}
        		if (!uField.getText().equals("") && !passField.getText().equals("") && !cpassField.getText().equals("") && user_type!=null && !eField.getText().equals(""))
        		{
        			if (password.equals(password1) && email_id.contains("@student.usm.my"))
        			{
        				if (user_type.equals("Admin"))
                		{
            				signin.setDisable(false);
                			try 
                			{
                				List<Admin> new_adminlist=get_AdminList();
                				List<Faculty> new_facultylist=get_FacultyList();
                				List<Student> new_studentlist=get_StudentList();
                				boolean found=validate(email_id, password, new_adminlist, new_facultylist, new_studentlist, passField, eField, passValidator, emailValidator);
                				if (found==false)
                				{
                					admin=new Admin();
                					admin.set_Profile(name, email_id, password, user_type);
                					admin.start(primaryStage, new_adminlist);
                					new_adminlist.add(admin);
                					serialize_Adminlist(new_adminlist);
                				}
                				else
                					found=false;
        					} 
                			catch (Exception e1) 
                			{
        						e1.printStackTrace();
        					}
                		}
                		if (user_type.equals("Faculty")) {
							signin.setDisable(false);
							try {
								List<Admin> new_adminlist = get_AdminList();
								List<Faculty> new_facultylist = get_FacultyList();
								List<Student> new_studentlist = get_StudentList();
								boolean found = validate(email_id, password, new_adminlist, new_facultylist, new_studentlist, passField, eField, passValidator, emailValidator);
								if (found == false) {
									faculty = new Faculty();
									faculty.set_Profile(name, email_id, password, user_type);
									faculty.start(primaryStage, new_facultylist);
									new_facultylist.add(faculty);
									serialize_Facultylist(new_facultylist);
								} else
									found = false;
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
						if (user_type.equals("Student"))
						{
							signin.setDisable(false);
							try
							{
								List<Admin> new_adminlist=get_AdminList();
								List<Faculty> new_facultylist=get_FacultyList();
								List<Student> new_studentlist=get_StudentList();
								boolean found=validate(email_id, password, new_adminlist, new_facultylist, new_studentlist, passField, eField, passValidator, emailValidator);
								if (found==false)
								{
									student=new Student();
									student.set_Profile(name, email_id, password, user_type, prog, branch_name, roll_no);
									student.start(primaryStage, new_studentlist);
									new_studentlist.add(student);
									serialize_Studentlist(new_studentlist);
								}
								else
									found=false;
							}
							catch (Exception e1)
							{
								e1.printStackTrace();
							}
						}
                		else
                		{
                			try
                            {
                            	prog=myComboBox1.getSelectionModel().getSelectedItem().toString();
                            }
                            catch (NullPointerException ex)
                            {
                            	ImageView imageView;
            					try 
            					{
            						imageView = new ImageView(new Image(new FileInputStream("Images/mark1.png")));
            	            		imageView.setFitHeight(17);
            	            		imageView.setFitWidth(20);
            	                	progValidator.getItems().clear();
            	                    MenuItem m4=new MenuItem("", imageView);
            	                    progValidator.getItems().add(m4);
            	                    progValidator.show(myComboBox1, Side.RIGHT, 10, 0);
            					} 
            					catch (FileNotFoundException e1)
            					{
            						e1.printStackTrace();
            					}
                            }
                			try
                            {
                            	branch_name=myComboBox2.getSelectionModel().getSelectedItem().toString();
                            }
                            catch (NullPointerException ex)
                            {
                            	ImageView imageView;
            					try 
            					{
            						imageView = new ImageView(new Image(new FileInputStream("Images/mark1.png")));
            	            		imageView.setFitHeight(17);
            	            		imageView.setFitWidth(20);
            	                	branchValidator.getItems().clear();
            	                    MenuItem m4=new MenuItem("", imageView);
            	                    branchValidator.getItems().add(m4);
            	                    branchValidator.show(myComboBox2, Side.RIGHT, 10, 0);
            					} 
            					catch (FileNotFoundException e1)
            					{
            						e1.printStackTrace();
            					}
                            }
                  			roll_no=rField.getText();
                  			if (rField.getText().equals("") || !rField.getText().matches("\\d*"))
                    		{
                  				rField.replaceText(0, rField.getText().length(), "");
                  				ImageView imageView;
								try 
								{
									imageView = new ImageView(new Image(new FileInputStream("Images/mark1.png")));
	                  				imageView.setFitHeight(17);
	                  				imageView.setFitWidth(20);
	                                rollValidator.getItems().clear();
	                                MenuItem m6=new MenuItem("", imageView);
	                                rollValidator.getItems().add(m6);
	                                rollValidator.show(rField, Side.RIGHT, 10, 0);
								} 
								catch (FileNotFoundException e1) 
								{
									e1.printStackTrace();
								}
                    		}

                		}
        			}
        			else if (!password.equals(password1))
        			{
        				ImageView imageView;
        				try 
    					{
        					imageView = new ImageView(new Image(new FileInputStream("Images/warn.png")));
              				imageView.setFitHeight(15);
              				imageView.setFitWidth(20);
    	                    cpassValidator.getItems().clear();
    	                    MenuItem m3=new MenuItem("Passwords Do Not Match", imageView);
    	                    m3.setStyle("-fx-text-fill: white;");
    	                    cpassValidator.getItems().add(m3);
    	                    cpassValidator.show(cpassField, Side.BOTTOM, 5, 0);
    					} 
    					catch (FileNotFoundException e1) 
    					{
    						e1.printStackTrace();
    					}
        				if (user_type.equals("Student")) 
        				{
        					try
                            {
                            	prog=myComboBox1.getSelectionModel().getSelectedItem().toString();
                            }
                            catch (NullPointerException ex)
                            {
                            	ImageView imageView1;
            					try 
            					{
            						imageView1 = new ImageView(new Image(new FileInputStream("Images/mark1.png")));
            	            		imageView1.setFitHeight(17);
            	            		imageView1.setFitWidth(20);
            	                	progValidator.getItems().clear();
            	                    MenuItem m4=new MenuItem("", imageView1);
            	                    progValidator.getItems().add(m4);
            	                    progValidator.show(myComboBox1, Side.RIGHT, 10, 0);
            					} 
            					catch (FileNotFoundException e1)
            					{
            						e1.printStackTrace();
            					}
                            }
                			try
                            {
                            	branch_name=myComboBox2.getSelectionModel().getSelectedItem().toString();
                            }
                            catch (NullPointerException ex)
                            {
                            	ImageView imageView1;
            					try 
            					{
            						imageView1 = new ImageView(new Image(new FileInputStream("Images/mark1.png")));
            	            		imageView1.setFitHeight(17);
            	            		imageView1.setFitWidth(20);
            	                	branchValidator.getItems().clear();
            	                    MenuItem m4=new MenuItem("", imageView1);
            	                    branchValidator.getItems().add(m4);
            	                    branchValidator.show(myComboBox2, Side.RIGHT, 10, 0);
            					} 
            					catch (FileNotFoundException e1)
            					{
            						e1.printStackTrace();
            					}
                            }
                  			roll_no=rField.getText();
                  			if (rField.getText().equals("") || !rField.getText().matches("\\d*"))
                    		{
                  				rField.replaceText(0, rField.getText().length(), "");
                  				ImageView imageView1;
    							try 
    							{
    								imageView1 = new ImageView(new Image(new FileInputStream("Images/mark1.png")));
                      				imageView1.setFitHeight(17);
                      				imageView1.setFitWidth(20);
                                    rollValidator.getItems().clear();
                                    MenuItem m6=new MenuItem("", imageView1);
                                    rollValidator.getItems().add(m6);
                                    rollValidator.show(rField, Side.RIGHT, 10, 0);
    							} 
    							catch (FileNotFoundException e1) 
    							{
    								e1.printStackTrace();
    							}
                    		}
              			}
        			}
        			if (!email_id.contains("@student.usm.my"))
        			{
        				ImageView imageView;
    					try 
    					{
    						imageView = new ImageView(new Image(new FileInputStream("Images/warn.png")));
    	        			imageView.setFitHeight(15);
    	        			imageView.setFitWidth(20);
    	                    emailValidator.getItems().clear();
    	                    MenuItem m5=new MenuItem("Incorrect Email ID", imageView);
    	                    m5.setStyle("-fx-text-fill: white;");
    	                    emailValidator.getItems().add(m5);
    	                    emailValidator.show(eField, Side.BOTTOM, 5, 0);
    					} 
    					catch (FileNotFoundException e1) 
    					{
    						e1.printStackTrace();
    					}
    					if (user_type.equals("Student")) 
    					{
    						try
                            {
                            	prog=myComboBox1.getSelectionModel().getSelectedItem().toString();
                            }
                            catch (NullPointerException ex)
                            {
                            	ImageView imageView1;
            					try 
            					{
            						imageView1 = new ImageView(new Image(new FileInputStream("Images/mark1.png")));
            	            		imageView1.setFitHeight(17);
            	            		imageView1.setFitWidth(20);
            	                	progValidator.getItems().clear();
            	                    MenuItem m4=new MenuItem("", imageView1);
            	                    progValidator.getItems().add(m4);
            	                    progValidator.show(myComboBox1, Side.RIGHT, 10, 0);
            					} 
            					catch (FileNotFoundException e1)
            					{
            						e1.printStackTrace();
            					}
                            }
                			try
                            {
                            	branch_name=myComboBox2.getSelectionModel().getSelectedItem().toString();
                            }
                            catch (NullPointerException ex)
                            {
                            	ImageView imageView1;
            					try 
            					{
            						imageView1 = new ImageView(new Image(new FileInputStream("Images/mark1.png")));
            	            		imageView1.setFitHeight(17);
            	            		imageView1.setFitWidth(20);
            	                	branchValidator.getItems().clear();
            	                    MenuItem m4=new MenuItem("", imageView1);
            	                    branchValidator.getItems().add(m4);
            	                    branchValidator.show(myComboBox2, Side.RIGHT, 10, 0);
            					} 
            					catch (FileNotFoundException e1)
            					{
            						e1.printStackTrace();
            					}
                            }
                  			roll_no=rField.getText();
                  			if (rField.getText().equals("") || !rField.getText().matches("\\d*"))
                    		{
                  				rField.replaceText(0, rField.getText().length(), "");
                  				ImageView imageView1;
    							try 
    							{
    								imageView1 = new ImageView(new Image(new FileInputStream("Images/mark1.png")));
                      				imageView1.setFitHeight(17);
                      				imageView1.setFitWidth(20);
                                    rollValidator.getItems().clear();
                                    MenuItem m6=new MenuItem("", imageView1);
                                    rollValidator.getItems().add(m6);
                                    rollValidator.show(rField, Side.RIGHT, 10, 0);
    							} 
    							catch (FileNotFoundException e1) 
    							{
    								e1.printStackTrace();
    							}
                    		}
              			}
        			}
        		}
        	}
		});
		
		uField.focusedProperty().addListener(new ChangeListener<Boolean>() 
		{
			@Override
            public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
			{
                if (newPropertyValue) 
                {
                    nameValidator.hide();
                }
            }
		});
		
		passField.focusedProperty().addListener(new ChangeListener<Boolean>() 
		{
			@Override
            public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
			{
                if (newPropertyValue) 
                {
                    passValidator.hide();
                }
            }
		});
		
		cpassField.focusedProperty().addListener(new ChangeListener<Boolean>() 
		{
			@Override
            public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
			{
                if (newPropertyValue) 
                {
                    cpassValidator.hide();
                }
            }
		});
		
		myComboBox.focusedProperty().addListener(new ChangeListener<Boolean>() 
		{
			@Override
            public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
			{
                if (newPropertyValue) 
                {
                    typeValidator.hide();
                }
            }
		});
		
		eField.focusedProperty().addListener(new ChangeListener<Boolean>() 
		{
			@Override
            public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
			{
                if (newPropertyValue) 
                {
                    emailValidator.hide();
                }
            }
		});
		
		myComboBox1.focusedProperty().addListener(new ChangeListener<Boolean>() 
		{
			@Override
            public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
			{
                if (newPropertyValue) 
                {
                    progValidator.hide();
                }
            }
		});
		
		myComboBox2.focusedProperty().addListener(new ChangeListener<Boolean>() 
		{
			@Override
            public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
			{
                if (newPropertyValue) 
                {
                    branchValidator.hide();
                }
            }
		});
		
		rField.focusedProperty().addListener(new ChangeListener<Boolean>() 
		{
			@Override
            public void changed(ObservableValue<? extends Boolean> arg0,Boolean oldPropertyValue, Boolean newPropertyValue) 
			{
                if (newPropertyValue) 
                {
                    rollValidator.hide();
                }
            }
		});
		final Button back=new Button("Back");
		back.setStyle("-fx-font: 25 arial; -fx-background-color: linear-gradient(#f0ff35, #a9ff00), radial-gradient(center 50% -40%, radius 200%, #b8ee36 45%, #80c800 50%);; -fx-background-radius: 15; -fx-border-color: silver; -fx-border-radius: 15;");
	    back.setLayoutX(340);
	    back.setLayoutY(440);
		back.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() 
	    {
			@Override
	        public void handle(MouseEvent e) 
	    	{
	    		back.setEffect(shadow);
	        }
	    });
	    back.addEventHandler(MouseEvent.MOUSE_EXITED,new EventHandler<MouseEvent>() 
		{
	    	@Override
	    	public void handle(MouseEvent e) 
	    	{
	    		back.setEffect(null);
	    	}
		});
        grid.add(back, 0, 9);
        back.setOnAction(new EventHandler<ActionEvent>()
        {
        	@Override
        	public void handle(ActionEvent e)
        	{
        		try 
        		{
					MainPage(primaryStage);
					nameValidator.hide();
					passValidator.hide();
					cpassValidator.hide();
					typeValidator.hide();
					emailValidator.hide();
					progValidator.hide();
					branchValidator.hide();
					rollValidator.hide();
				} 
        		catch (FileNotFoundException e1) 
        		{
					e1.printStackTrace();
				}
        	}
        });   
        Label n=new Label("Create an account");
        n.setFont(Font.font("sans serif", FontWeight.BOLD, 40));
        n.setTextFill(Color.GHOSTWHITE);
        n.setAlignment(Pos.CENTER);
		Pane pane1=new Pane();
		final ImageView imageView3 = new ImageView(new Image(new FileInputStream("Images/img.jpg")));
		imageView3.setFitHeight(730);
		imageView3.setFitWidth(1290);
		imageView3.setLayoutX(5);
		imageView3.setLayoutY(5);
		pane1.getChildren().add(imageView3);
		final Pane pane2=new Pane();
		pane2.setLayoutX(550);
		pane2.setLayoutY(30);
		Pane pane3=new Pane();
		pane3.getChildren().add(imageView1);
		pane3.setLayoutX(260);
		pane3.setLayoutY(20);
		pane2.getChildren().add(pane3);
		grid.setLayoutY(150);
		pane2.getChildren().add(grid);
		pane2.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10;");
		pane1.getChildren().add(pane2);
		Pane pane4=new Pane();
		pane4.getChildren().add(n);
		pane4.setLayoutX(100);
		pane4.setLayoutY(350);
		pane1.getChildren().add(pane4);
		Pane masterPane=new Pane();
		masterPane.getChildren().addAll(pane1);
        final Scene scene=new Scene(masterPane);
        signin.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            pane2.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    signin.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	            pane2.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
	    back.setOnMouseEntered(new EventHandler() 
	    {
	    	@Override
			public void handle(Event me) {
	            pane2.setCursor(Cursor.HAND); //Change cursor to hand
			}
	    });
	    back.setOnMouseExited(new EventHandler() 
	    {
	    	@Override
	        public void handle(Event me) {
	            pane2.setCursor(Cursor.DEFAULT); //Change cursor to crosshair
	        }
	    });
        primaryStage.setScene(scene);
        primaryStage.show();
		}
		
		/**
		 * Type user.
		 *
		 * @param myComboBox the my combo box
		 */
		private void TypeUser(ComboBox<String> myComboBox) 
		{
			myComboBox.getItems().add("Admin");
			myComboBox.getItems().add("Faculty");
			myComboBox.getItems().add("Student");
		}
		
		/**
		 * Program.
		 *
		 * @param myComboBox the my combo box
		 */
		private void Program(ComboBox<String> myComboBox)
		{
			myComboBox.getItems().add("B.Tech Ist Year");
			myComboBox.getItems().add("B.Tech IInd Year");
			myComboBox.getItems().add("B.Tech IIIrd Year");
			myComboBox.getItems().add("B.Tech IVth Year");
			myComboBox.getItems().add("M.Tech");
			myComboBox.getItems().add("PhD");
		}
		
		/**
		 * Branch.
		 *
		 * @param myComboBox the my combo box
		 */
		private void Branch(ComboBox<String> myComboBox)
		{
			myComboBox.getItems().add("CSE");
			myComboBox.getItems().add("ECE");
			myComboBox.getItems().add("CSAM");
		}
		
		/**
		 * Gets the admin list.
		 *
		 * @return the admin list
		 * @throws IOException Signals that an I/O exception has occurred.
		 * @throws ClassNotFoundException the class not found exception
		 */
		public static List<Admin> get_AdminList() throws IOException, ClassNotFoundException  //deserialize
		{
	        ObjectInputStream inFile=null;
	        try
	        {
	        	inFile=new ObjectInputStream(new FileInputStream("./src/AdminList"));
	        	List<Admin> list=new ArrayList<Admin>();
	        	list=(List<Admin>) inFile.readObject();
	        	return list;
	        }
	        finally
	        {
	        	inFile.close();
	        	
	        }
		}
		
		/**
		 * Gets the faculty list.
		 *
		 * @return the faculty list
		 * @throws IOException Signals that an I/O exception has occurred.
		 * @throws ClassNotFoundException the class not found exception
		 */
		public static List<Faculty> get_FacultyList() throws IOException, ClassNotFoundException  //deserialize
		{
	        ObjectInputStream inFile=null;
	        try
	        {
	        	inFile=new ObjectInputStream(new FileInputStream("./src/FacultyList"));
	        	List<Faculty> list=new ArrayList<Faculty>();
	        	list=(List<Faculty>) inFile.readObject();
	        	return list;
	        }
	        finally
	        {
	        	inFile.close();
	        	
	        }
		}
		
		/**
		 * Gets the student list.
		 *
		 * @return the student list
		 * @throws IOException Signals that an I/O exception has occurred.
		 * @throws ClassNotFoundException the class not found exception
		 */
		public static List<Student> get_StudentList() throws IOException, ClassNotFoundException  //deserialize
		{
	        ObjectInputStream inFile=null;
	        try
	        {
	        	inFile=new ObjectInputStream(new FileInputStream("./src/StudentList"));
	        	List<Student> list=new ArrayList<Student>();
	        	list=(List<Student>) inFile.readObject();
	        	return list;
	        }
	        finally
	        {
	        	inFile.close();
	        	
	        }
		}
		
		/**
		 * Serialize adminlist.
		 *
		 * @param adminlist the adminlist
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static void serialize_Adminlist(List<Admin> adminlist) throws IOException
		{
	        ObjectOutputStream outFile=null;
	        try
	        {
	        	outFile=new ObjectOutputStream(new FileOutputStream("./src/AdminList"));
	        	outFile.writeObject(adminlist);
	        }
	        finally
	        {
	        	outFile.close();
	        }
		}
		
		/**
		 * Serialize facultylist.
		 *
		 * @param facultylist the facultylist
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static void serialize_Facultylist(List<Faculty> facultylist) throws IOException
		{
	        ObjectOutputStream outFile=null;
	        try
	        {
	        	outFile=new ObjectOutputStream(new FileOutputStream("./src/FacultyList"));
	        	outFile.writeObject(facultylist);
	        }
	        finally
	        {
	        	outFile.close();
	        }
		}
		
		/**
		 * Serialize studentlist.
		 *
		 * @param studentlist the studentlist
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static void serialize_Studentlist(List<Student> studentlist) throws IOException
		{
	        ObjectOutputStream outFile=null;
	        try
	        {
	        	outFile=new ObjectOutputStream(new FileOutputStream("./src/StudentList"));
	        	outFile.writeObject(studentlist);
	        }
	        finally
	        {
	        	outFile.close();
	        }
		}
		
		/**
		 * Validate.
		 *
		 * @param email the email
		 * @param password the password
		 * @param new_adminlist the new adminlist
		 * @param new_facultylist the new facultylist
		 * @param new_studentlist the new studentlist
		 * @param cpassField the cpass field
		 * @param eField the e field
		 * @param passValidator the pass validator
		 * @param emailValidator the email validator
		 * @return true, if successful
		 */
		private boolean validate(String email, String password, List<Admin> new_adminlist, List<Faculty> new_facultylist, List<Student> new_studentlist, TextField cpassField, TextField eField, ContextMenu passValidator, ContextMenu emailValidator)
		{
			for (Admin ad : new_adminlist)
			{
				if (ad.getProfile().getemail().equals(email_id) && ad.getProfile().getpassword().equals(password))
				{
					ImageView imageView;
    				try 
					{
    					imageView = new ImageView(new Image(new FileInputStream("Images/warn.png")));
          				imageView.setFitHeight(15);
          				imageView.setFitWidth(20);
	                    passValidator.getItems().clear();
	                    MenuItem m3=new MenuItem("Password already exists", imageView);
	                    m3.setStyle("-fx-text-fill: white;");
	                    passValidator.getItems().add(m3);
	                    passValidator.show(cpassField, Side.BOTTOM, 5, 0);
					} 
					catch (FileNotFoundException e1) 
					{
						e1.printStackTrace();
					}
					ImageView imageView1;
					try 
					{
						imageView1 = new ImageView(new Image(new FileInputStream("Images/warn.png")));
	        			imageView1.setFitHeight(15);
	        			imageView1.setFitWidth(20);
	                    emailValidator.getItems().clear();
	                    MenuItem m5=new MenuItem("Email ID already exists", imageView1);
	                    m5.setStyle("-fx-text-fill: white;");
	                    emailValidator.getItems().add(m5);
	                    emailValidator.show(eField, Side.BOTTOM, 5, 0);
					} 
					catch (FileNotFoundException e1) 
					{
						e1.printStackTrace();
					}
					return true;
				}
			}
			for (Faculty fac : new_facultylist)
			{
				if (fac.getProfile().getemail().equals(email_id) && fac.getProfile().getpassword().equals(password))
				{
					ImageView imageView;
    				try 
					{
    					imageView = new ImageView(new Image(new FileInputStream("Images/warn.png")));
          				imageView.setFitHeight(15);
          				imageView.setFitWidth(20);
	                    passValidator.getItems().clear();
	                    MenuItem m3=new MenuItem("Password already exists", imageView);
	                    m3.setStyle("-fx-text-fill: white;");
	                    passValidator.getItems().add(m3);
	                    passValidator.show(cpassField, Side.BOTTOM, 5, 0);
					} 
					catch (FileNotFoundException e1) 
					{
						e1.printStackTrace();
					}
					ImageView imageView1;
					try 
					{
						imageView1 = new ImageView(new Image(new FileInputStream("Images/warn.png")));
	        			imageView1.setFitHeight(15);
	        			imageView1.setFitWidth(20);
	                    emailValidator.getItems().clear();
	                    MenuItem m5=new MenuItem("Email ID already exists", imageView1);
	                    m5.setStyle("-fx-text-fill: white;");
	                    emailValidator.getItems().add(m5);
	                    emailValidator.show(eField, Side.BOTTOM, 5, 0);
					} 
					catch (FileNotFoundException e1) 
					{
						e1.printStackTrace();
					}
					return true;
				}
			}
			for (Student stu : new_studentlist)
			{
				if (stu.getProfile().getemail().equals(email_id) && stu.getProfile().getpassword().equals(password))
				{
					ImageView imageView;
    				try 
					{
    					imageView = new ImageView(new Image(new FileInputStream("Images/warn.png")));
          				imageView.setFitHeight(15);
          				imageView.setFitWidth(20);
	                    passValidator.getItems().clear();
	                    MenuItem m3=new MenuItem("Password already exists", imageView);
	                    m3.setStyle("-fx-text-fill: white;");
	                    passValidator.getItems().add(m3);
	                    passValidator.show(cpassField, Side.BOTTOM, 5, 0);
					} 
					catch (FileNotFoundException e1) 
					{
						e1.printStackTrace();
					}
					ImageView imageView1;
					try 
					{
						imageView1 = new ImageView(new Image(new FileInputStream("Images/warn.png")));
	        			imageView1.setFitHeight(15);
	        			imageView1.setFitWidth(20);
	                    emailValidator.getItems().clear();
	                    MenuItem m5=new MenuItem("Email ID already exists", imageView1);
	                    m5.setStyle("-fx-text-fill: white;");
	                    emailValidator.getItems().add(m5);
	                    emailValidator.show(eField, Side.BOTTOM, 5, 0);
					} 
					catch (FileNotFoundException e1) 
					{
						e1.printStackTrace();
					}
					return true;
				}
			}
			return false;
		}
}

/**
 * The Class Room.
 */
class Room implements Serializable
{
	
	/** The time slot. */
	private String time_slot, room_allotted, audience, course_name, date, user;   // course_name also servers purpose
	
	/** The capacity. */
	private int capacity=300, day;
	
	/**
	 * Sets the day.
	 *
	 * @param d the new day
	 */
	public void setday(int d)
	{
		day=d;
	}
	
	/**
	 * Sets the time.
	 *
	 * @param time the new time
	 */
	public void settime(String time)
	{
		time_slot=time;
	}
	
	/**
	 * Sets the room.
	 *
	 * @param room the new room
	 */
	public void setroom(String room)
	{
		room_allotted=room;
	}
	
	/**
	 * Sets the audience.
	 *
	 * @param target the new audience
	 */
	public void setaudience(String target)
	{
		audience=target;
	}
	
	/**
	 * Sets the course.
	 *
	 * @param course the new course
	 */
	public void setcourse(String course)
	{
		course_name=course;
	}
	
	/**
	 * Sets the capacity.
	 *
	 * @param cap the new capacity
	 */
	public void setcapacity(int cap)
	{
		capacity=cap;
	}
	
	/**
	 * Sets the date.
	 *
	 * @param d the new date
	 */
	public void setdate(String d)
	{
		date=d;
	}
	
	/**
	 * Sets the user.
	 *
	 * @param u the new user
	 */
	public void setuser(String u)
	{
		user=u;
	}
	
	/**
	 * Gets the course.
	 *
	 * @return the course
	 */
	public String getcourse()
	{
		return course_name;
	}
	
	/**
	 * Gets the day.
	 *
	 * @return the day
	 */
	public int getDay()
	{
		return day;
	}
	
	/**
	 * Gets the time.
	 *
	 * @return the time
	 */
	public String gettime()
	{
		return time_slot;
	}
	
	/**
	 * Gets the room.
	 *
	 * @return the room
	 */
	public String getRoom()
	{
		return room_allotted;
	}
	
	/**
	 * Gets the audience.
	 *
	 * @return the audience
	 */
	public String getAudience()
	{
		return audience;
	}
	
	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public String getdate()
	{
		return date;
	}
	
	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public String getUser()
	{
		return user;
	}
	
	/**
	 * Display.
	 */
	public void display()
	{
		System.out.println(course_name+" "+day+" "+time_slot+" "+room_allotted+" "+audience+" "+capacity+" "+date+" "+user);
	}
}

/**
 * The Class Course.
 */
class Course implements Serializable
{
	
	/** The course name. */
	private String course_name, code, instructor, acronym, course_type;
	
	/** The credits. */
	private int credits;
	
	/** The pc. */
	private List<String> pc;
	
	/** The room. */
	private List<Room> room;
	
	/**
	 * Instantiates a new course.
	 */
	Course()
	{
		pc=new ArrayList<String>();
		room=new ArrayList<Room>();
	}
	
	/**
	 * Sets the course.
	 *
	 * @param course the new course
	 */
	public void setcourse(String course)
	{
		course_name=course;
	}
	
	/**
	 * Gets the course.
	 *
	 * @return the course
	 */
	public String getcourse()
	{
		return course_name;
	}
	
	/**
	 * Sets the code.
	 *
	 * @param c the new code
	 */
	public void setcode(String c)
	{
		code=c;
	}
	
	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public String getcode()
	{
		return code;
	}
	
	/**
	 * Sets the coursetype.
	 *
	 * @param type the new coursetype
	 */
	public void setcoursetype(String type)
	{
		course_type=type;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String gettype()
	{
		return course_type;
	}
	
	/**
	 * Sets the instructor.
	 *
	 * @param name the new instructor
	 */
	public void setInstructor(String name)
	{
		instructor=name;
	}
	
	/**
	 * Gets the instructor.
	 *
	 * @return the instructor
	 */
	public String getInstructor()
	{
		return instructor;
	}
	
	/**
	 * Sets the credits.
	 *
	 * @param cr the new credits
	 */
	public void setcredits(int cr)
	{
		credits=cr;
	}
	
	/**
	 * Gets the credits.
	 *
	 * @return the credits
	 */
	public int getcredits()
	{
		return credits;
	}
	
	/**
	 * Sets the acronym.
	 *
	 * @param ac the new acronym
	 */
	public void setacronym(String ac)
	{
		acronym=ac;
	}
	
	/**
	 * Gets the acronym.
	 *
	 * @return the acronym
	 */
	public String getacronym()
	{
		return acronym;
	}
	
	/**
	 * Findroominfo.
	 *
	 * @param rooms the rooms
	 */
	public void findroominfo(List<Room> rooms)
	{
		for (int i=0; i<rooms.size(); i++)
		{
			if (rooms.get(i).getcourse().equals(course_name))
				room.add(rooms.get(i));
		}
		for (int i=0; i<room.size(); i++)
		{
			//room.get(i).display();
		}
	}
	
	/**
	 * Gets the room list.
	 *
	 * @return the room list
	 */
	public List<Room> getRoomList()
	{
		return room;
	}
	
	/**
	 * Adds the pc.
	 *
	 * @param post_c the post c
	 */
	public void add_pc(String post_c)
	{
		pc.add(post_c);
	}
	
	/**
	 * Gets the pc.
	 *
	 * @return the pc
	 */
	public List<String> getPc()
	{
		return pc;
	}
	
	/**
	 * Display.
	 */
	public void display()
	{
		System.out.println(course_name);
		System.out.println(course_type);
		System.out.println(code);
		System.out.println(credits);
		System.out.println(instructor);
		System.out.println(acronym);
		for (int i=0; i<room.size(); i++)
		{
			room.get(i).display();
		}
		for (int i=0; i<pc.size(); i++)
		{
			System.out.println(pc.get(i));
		}
	}
}

/**
 * The Class CourseDatabase.
 */
class CourseDatabase implements Serializable
{
	
	/** The course. */
	Course course;
	
	/** The course list. */
	private List<Course> course_list;
	
	/**
	 * Instantiates a new course database.
	 */
	CourseDatabase()
	{
		course_list=new ArrayList<Course>();
	}
	
	/**
	 * Serialize.
	 *
	 * @param course_list the course list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void serialize(List<Course> course_list) throws IOException
	{
        ObjectOutputStream outFile=null;
        try
        {
        	outFile=new ObjectOutputStream(new FileOutputStream("./src/Monsoon2017_Course_info"));
        	outFile.writeObject(course_list);
        }
        finally
        {
        	outFile.close();
        }
	}
	
	/**
	 * Deserialize.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	@SuppressWarnings("unchecked")
	public static List<Course> deserialize() throws IOException, ClassNotFoundException
	{
		ObjectInputStream inFile=null;
		try
        {
        	inFile=new ObjectInputStream(new FileInputStream("./src/Monsoon2017_Course_info"));
        	List<Course> list=new ArrayList<Course>();
        	list=(List<Course>) inFile.readObject();
        	return list;
        }
        finally
        {
        	inFile.close();
        }
	}
	
	/**
	 * Retrieve course.
	 *
	 * @param rooms the rooms
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void retrieve_course(List<Room> rooms) throws IOException
	{
		BufferedReader infile=new BufferedReader(new FileReader("./src/Post_Conditions.csv"));
		infile.readLine();
		String nextline=infile.readLine();
		while (nextline!=null)
		{
			course=new Course();
			String []fields=nextline.split(";");
			fields[0]=fields[0].replace('\"', ' ');
			course.setcode(fields[0].trim());
			course.setcourse(fields[1].trim());
			course.findroominfo(rooms);
			course.setInstructor(fields[2].trim());
			BufferedReader infile1=new BufferedReader(new FileReader("./src/AP_Project.csv"));
			infile1.readLine();
			infile1.readLine();
			String nextline1=infile1.readLine();
			while (nextline1!=null)
			{
				if (nextline1.contains(fields[1].trim()))
				{
					String []var=nextline1.split(",");
					var[0]=var[0].replace("\"", "");
					course.setcoursetype(var[0].trim());
					course.setacronym(var[5].trim());
					course.setcredits(Integer.parseInt(var[4].trim()));
					break;
				}
				nextline1=infile1.readLine();
			}
			for (int i=3; i<fields.length; i++)
			{
				fields[i]=fields[i].replace('\"', ' ');
				course.add_pc(fields[i].trim());
			}
			course_list.add(course);
			nextline=infile.readLine();
			infile1.close();
		}
		infile.close();
		serialize(course_list);
	}
}
