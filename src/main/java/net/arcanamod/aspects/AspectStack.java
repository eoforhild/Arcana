package net.arcanamod.aspects;

public class AspectStack {

	public static final AspectStack EMPTY = new AspectStack(Aspect.EMPTY, 0);

	private boolean isEmpty;
	private int amount;
	private Aspect aspect;

	public boolean isEmpty() {
		return isEmpty;
	}

	public void setAspect(Aspect aspect) {
		this.aspect = aspect;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Aspect getAspect() {
		return aspect;
	}

	public AspectStack(){
		this(Aspect.EMPTY,0);
	}

	public AspectStack(Aspect aspect){
		this(aspect,1);
	}

	public AspectStack(Aspect aspect, int amount){
		this.isEmpty = amount == 0 || aspect == Aspect.EMPTY;

		this.aspect = !this.isEmpty ? aspect : Aspect.EMPTY;
		this.amount = !this.isEmpty ? amount : 0;
	}

	@Override
	public String toString() {
		return "AspectStack{" +
				"amount=" + amount +
				", aspect=" + aspect +
				'}';
	}
}