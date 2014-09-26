package spytools.multi.generators;

/**
 * Static Generator appends/prepends a value to an existing Generator
 * It will use the provided "other" Generator's methods to do most of the work;  
 * 
 * @author smitc
 */
public class StaticGenerator extends AbstractGeneratorInfo{
	private enum preAppend{PREPEND, APPEND}
	
	private final preAppend preApp;
	private final String guess;
	private final AbstractGeneratorInfo generator;
	
	
	public StaticGenerator(String info, AbstractGeneratorInfo generator){
		this(info, generator, preAppend.PREPEND);
	}
	
	public StaticGenerator(AbstractGeneratorInfo generator, String info){
		this(info, generator, preAppend.APPEND);
	}
	
	private StaticGenerator(String info, AbstractGeneratorInfo generator, preAppend preApp){
		this.guess = info;
		this.generator = generator;
		this.preApp = preApp;
	}
	
	@Override
	protected void initializeInfo() {
		if(this.generator != null){
			this.generator.initializeInfo();
		} 		
	}

	@Override
	public int getNeededThreads() {
		if(this.generator != null){
			return this.generator.getNeededThreads();
		} 
		return 1;
	}

	@Override
	public String generateNextGuess() {
		String guess = null;
		switch(this.preApp){
		case PREPEND:
			if(this.generator != null){
				guess = this.guess + this.generator.generateNextGuess();
			}
			break;
		case APPEND:
			if(this.generator != null){
				guess = this.generator.generateNextGuess() + this.guess;
			}
			break;
		default:
			//TODO throw
			break;
		}
		return guess;
	}

	@Override
	public String toString() {
		String info = "";
		if(this.generator != null){
			info = this.generator.toString();
		}
		String additional = "";
		switch(this.preApp){
		case PREPEND:
			additional = "StaticPrependGenerator";
			break;
		case APPEND:
			additional = "StaticAppendGenerator";
			break;
		default:
			//TODO throw
			break;
		}
		info += " " + additional;
		return info;
	}

}
