package spytools.multi.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import spytools.multi.custom.execplan.ExecutionType;
import spytools.multi.custom.generators.GeneratorInfo;
import spytools.multi.helpers.Logger;
import spytools.multi.helpers.SetupException;
import spytools.multi.helpers.SingleGuess;
import spytools.multi.helpers.ThreadNotifier;
import spytools.multi.helpers.ThreadNotifier.ThreadType;

public class ProducerManagement extends ManagementThread implements Runnable{
	private ExecutionType exType;
	private List<GeneratorInfo> gens;
	private Map<String, BlockingQueue <SingleGuess>> collectionQueue;
	
	private int numProducerThreads;
	private ExecutorService exec; //manages sub threads
	
	private int unusedThreads;
	
	private final Logger log = new Logger();
	private ThreadNotifier notifier = ThreadNotifier.getInstance();
	
	public ProducerManagement(ExecutionType exType, int producerThreads, GeneratorInfo... gen) throws SetupException{
		this.exType = exType;
		this.gens = new ArrayList<GeneratorInfo>();
		for(GeneratorInfo g : gen){
			this.gens.add(g);
		}
		
		int neededThreads = determineNeededThreads(gen);
		if(neededThreads > producerThreads)
			throw new SetupException("Not enough threads allocated for execution");
		
		this.collectionQueue = exType.generateQueues(gen.length);
		this.numProducerThreads = producerThreads;
		this.exec = Executors.newFixedThreadPool(this.numProducerThreads);
	}
	
	public static int determineNeededThreads(GeneratorInfo... gen){
		int neededThreads = 0;
		for(GeneratorInfo g : gen){
			neededThreads += g.getNeededThreads();
		}
		return neededThreads;
	}
	
	public int getUnusedThreads(){
		return this.unusedThreads;
	}
	
	@Override
	public void run() {
		int i = 0;
		for(GeneratorInfo gen : this.gens){
			final int curThread = i;
			gen.init(curThread, this.numProducerThreads, this.collectionQueue);
			this.exec.execute(gen);
			i++;
		}
		//this is long running, it stops when there are no more guesses to generate or the thread has been halted
		try {
			generateGuesses();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		notifyDone();
		shutdown();
	}
	
	private void generateGuesses() throws InterruptedException{
		GeneratorInfo[] gens = this.exType.getGenerators();
		SingleGuess[] guesses = new SingleGuess[gens.length];
		generateGuess(gens, 0, gens.length - 1,  guesses);
	}
	
	private void generateGuess(GeneratorInfo[] gens, int index, int maxIndex, SingleGuess[] guesses) throws InterruptedException{
		GeneratorInfo g = gens[index];
		BlockingQueue<SingleGuess> q = g.getQueue();
		int numNulls = g.getTotalThreadNum();
		while(!this.notifier.shouldHalt(ThreadType.PRODUCER_MANAGEMENT) || !this.notifier.shouldHalt(ThreadType.PRODUCER_THREAD)){
			SingleGuess guess = q.take();
			if(guess.toString() == null){
				if(--numNulls == 0){
					break;
				}
				continue;
			}
			guesses[index] = guess;
			if(index < maxIndex)
				generateGuess(gens, index + 1, maxIndex, guesses);
			else
				this.exType.addGuessObject(this.exType.makeGuessObject(gens, guesses));
		}
	}

	@Override
	public void notifyDone(){
		this.notifier.setDone(ThreadType.PRODUCER_THREAD);
		this.notifier.setDone(ThreadType.PRODUCER_MANAGEMENT);
		for(String s: this.collectionQueue.keySet()){
			this.collectionQueue.get(s).clear();
		}
	}
	
	@Override
	public void shutdown(){
		try{
			this.log.info("ProducerThreads shutdown");

			this.notifier.haltThread(ThreadType.PRODUCER_THREAD);
			this.exec.shutdown();
			this.exType.clearCollector(this.collectionQueue);
			this.exec.awaitTermination(1, TimeUnit.SECONDS);
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			this.exec.shutdownNow();
			for(String s: this.collectionQueue.keySet()){
				this.collectionQueue.get(s).clear();
			}
			this.notifier.haltThread(ThreadType.PRODUCER_MANAGEMENT);
			this.log.info("ProducerThreads complete");
		}
	}
	
	

	
}
