package edu.unm.ece.drake.rectifier;

public interface Pipeline<T> {
	T execute(T obj);
}
