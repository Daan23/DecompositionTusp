package mspInstance;


public class RequiredResource {
	private Resource 	resource; 
	private int 		amount;
	
	
	public RequiredResource(Resource r1, int amount) {
		this.resource = r1; 
		this.amount = amount; 
	}
	public Resource getResource() {
		return resource;
	}
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	} 
	
	public String toString() {
		return this.resource.getName() + this.amount; 
	}

}
