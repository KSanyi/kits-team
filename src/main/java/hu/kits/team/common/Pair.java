package hu.kits.team.common;

import java.util.Objects;

public class Pair<S, T> {

	public final S first;
	
	public final T second;

	public Pair(S first, T second) {
		this.first = first;
		this.second = second;
	}
	
	public S first() {
	    return first;
	}
	
	public T second() {
        return second;
    }
	
	@Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object other) {
        if(other == this) return true;
        if(other == null || !(other instanceof Pair)) return false;
        Pair<S,T> otherPair = (Pair<S,T>)other;
        return otherPair.first.equals(first) && otherPair.second.equals(second);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
	
}
