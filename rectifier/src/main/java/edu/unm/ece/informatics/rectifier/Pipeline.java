package edu.unm.ece.informatics.rectifier;

public interface Pipeline<T> {
	T execute(T obj);
}
