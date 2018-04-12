//java Calculator1.0版 by lxy
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.regex.Pattern;	//正则表达式模式匹配数字
public class CalculatorTest {
	public static void main(String[] args) {
		new CalFrame();
		
	}
}

class CalFrame extends Frame {
	Panel p1 = new Panel();
	String StartString = "请输入...";
	TextField TField = new TextField(StartString);
	String input = "";
	boolean RestFlag = false ;
	CalFrame() {
		super("My first Calculator By Java");
		setBackground(new Color(204,204,255));
		setLayout(new BorderLayout());//
		setBounds(200,200,400,400);
		Panel1Set();
		TField.setFont(new Font("宋体",Font.PLAIN,20));
		add(TField,BorderLayout.NORTH);
		add(p1,BorderLayout.CENTER);
		//pack();
		
		this.addWindowListener( new WindowAdapter() {		//监听Windows
			public void windowClosing(WindowEvent e) {
					setVisible(false);
					System.exit(0);
				}
			}
		);
		
		setVisible(true);
	}
	
	void Panel1Set() {
		p1.setBackground(new Color(204,204,255));
		p1.setLayout(new GridLayout(4, 5,5,5));//
		Button[] button = new Button[20];
		String[] buName = {"7","8","9","+","Rest","4","5","6","-","C","1","2","3","*","(","0",".","=","/",")"} ;
		ButtonActionMonitor BAM = new ButtonActionMonitor();
		for(int i = 0;i<20;i++)
		{
			button[i] = new Button(buName[i]);
			button[i].setBackground(new Color(192,192,192));  //设置按键的背景色
			button[i].setFont(new Font("宋体",Font.PLAIN,20));
			if ((i+1) % 5 == 4)
      	button[i].setForeground(Color.BLUE);
			button[i].addActionListener(BAM);	//添加监听器
			p1.add(button[i]);
		}
		button[17].setForeground(Color.RED);
		
		p1.setVisible(true);
	}
	
	ArrayList<String> infixToSuffix(ArrayList<String> infix) {
		Stack<String> s = new Stack<String>();
		ArrayList<String> suffix = new ArrayList<String> ();
		
		for(int i = 0;i < infix.size(); i++) {
			String tmp = infix.get(i);
			String c = "";
			switch (tmp) {
				case " ":
					break;
				case "(":
					s.push(tmp);	break;
				case "+":
				case "-":
					while(s.size() != 0) {
						c = s.pop();
						if(c.equals("(")) {
							s.push("("); break;
						}
						
						suffix.add(c) ;
					}
					s.push(tmp);
					break;	
				case "*":
				case "/":
					while(s.size() != 0) {
						c = s.pop();
						if(c.equals("+") || c.equals("-") || c.equals("(")) {
							s.push(c); 
							break;
						} else {
							suffix.add(c) ;
						}
					}
					s.push(tmp);
					break;
				case ")":
					while(!s.isEmpty()) {
						c = s.pop();
						if(c.equals("(")) {
							break;
						} else {
							suffix.add(c) ;
						}
					}
					break;
				default:
					suffix.add(tmp);
					break;
			}
		}
		while(s.size() != 0) {
			suffix.add(s.pop());
		}		
		return suffix;
	}
	
	Double suffixToNum (ArrayList<String> suffix) {
		Stack<Double> stack = new Stack<Double>();
		 // 使用正则表达式匹配数字
		Pattern pattern = Pattern.compile("\\d+||(\\d+\\.\\d+)");
		
		for(int i = 0; i < suffix.size(); i++) {
			 if (suffix.get(i).equals("")) {
       		continue;
       }
       if (pattern.matcher(suffix.get(i)).matches()) {
          stack.push(Double.parseDouble(suffix.get(i)));		//匹配数字
       }
            // 如果是运算符，则弹出栈顶的两个数进行计算
       else {
          double y = stack.pop();
          double x = stack.pop();
          stack.push(calculate(x, y, suffix.get(i))); // 将运算结果重新压栈
       }
		}
		return stack.pop();
	}
	
	Double calculate(double x, double y, String string) {
		if(string.trim().equals("+")) {
			return x + y;
		}
		if(string.trim().equals("-")) {
			return x - y;
		}
		if(string.trim().equals("*")) {
			return x * y;
		}
		if(string.trim().equals("/")) {
			return x / y;
		}
		return (double) 0;
	}
	
	ArrayList<String> DataHandle(String input) {
		ArrayList<String> c = new ArrayList<String>();
		Queue<Character> queue = new LinkedList<Character>();
		String numStr = "";
		for(int i=0; i<input.length(); i++) {
			char tmp = input.charAt(i);
			if(Character.isDigit(tmp) || tmp == '.') {
				queue.offer(tmp);
			} else {
				while(queue.size() > 0) {
					numStr += queue.poll();
				}
				if(numStr.length()>0)
					c.add(numStr);
				c.add(String.valueOf(tmp));
				numStr = "";
			}
		}
		while(queue.size() > 0) {		//清空队列
			numStr += queue.poll();
		}
		if(numStr.length()>0)
			c.add(numStr);
		numStr = "";
		
		return c;
	}
	
	void Compute(String Input) {
		Double result = 0.0;
		try {
			result = suffixToNum(infixToSuffix(DataHandle(Input)));
			input = result.toString();
		}catch (Exception e) {
			input = "请输入合法的表达式！";
			return ;
		}
		
		System.out.println(result);
	}
	
	class ButtonActionMonitor implements ActionListener {
		public void actionPerformed (ActionEvent e) {	
			String tmpStr = e.getActionCommand();
			System.out.println("a button has been pressed  " + tmpStr);
			if (tmpStr == "=") {
				RestFlag = true ;
				Compute(input);
			} else if (tmpStr == "Rest") {
				input = StartString;
				RestFlag = true ;
			} else if (tmpStr == "C") {	//退格
				input = input.substring(0,input.length() - 1);
			}else {
				if (RestFlag == true) {
					input = "";RestFlag = false;	//按过等号键后复位操作
				}
				input += tmpStr;
			}
			TField.setText(input);
		}
	}
}