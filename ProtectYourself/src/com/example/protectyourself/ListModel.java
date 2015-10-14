package com.example.protectyourself;


public class ListModel{
	 

	    private String name;
	    private String number;
	    
	    public ListModel(String name, String number) {
	        super();
	        
	        this.setName(name);
	        this.setNumber(number);
	        
	    }

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}
	    
}