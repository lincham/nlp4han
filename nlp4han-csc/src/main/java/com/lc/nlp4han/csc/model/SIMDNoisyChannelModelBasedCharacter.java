package com.lc.nlp4han.csc.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import com.lc.nlp4han.csc.ngram.NGramModel;
import com.lc.nlp4han.csc.util.ConfusionSet;
import com.lc.nlp4han.csc.util.Dictionary;
import com.lc.nlp4han.csc.util.Sentence;

/**
 *<ul>
 *<li>Description: 在SIMD噪音通道模型的基础上，引入字的概率  
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年11月16日
 *</ul>
 */
public class SIMDNoisyChannelModelBasedCharacter extends AbstractNoisyChannelModel {
	
	private Dictionary dictionary;
	
	public SIMDNoisyChannelModelBasedCharacter(Dictionary dictionary, NGramModel nGramModel,
			ConfusionSet confusionSet) throws IOException {
		super(confusionSet, nGramModel);
		
		this.dictionary = dictionary;
	}
	
	public SIMDNoisyChannelModelBasedCharacter(Dictionary dictionary, NGramModel nGramModel,
			ConfusionSet confusionSet, double magicNumber) throws IOException {
		super(confusionSet, nGramModel, magicNumber);
		
		this.dictionary = dictionary;
	}

	@Override
	public Sentence getBestSentence(Sentence sentence) {
		return getBestKSentence(sentence, 1).get(0);
	}
	
	@Override
	public ArrayList<Sentence> getBestKSentence(Sentence sentence, int k) {
		if(k < 1)
			throw new IllegalArgumentException("返回候选句子数目不能小于1");
		beamSize = k;
		ArrayList<Integer> errorLocations = getErrorLocationsBySIMD(dictionary, sentence);
		ArrayList<Sentence> res = new ArrayList<>();
		
		//连续单字词的最大个数小于2，不作处理直接返回原句
		if(errorLocations.size() > 1) {
			res = beamSearch(confusionSet, beamSize, sentence, errorLocations);
			return res;
		}
		
		res.add(sentence);
		return res;
	}

	@Override
	public double getSourceModelLogScore(Sentence candidate) {
		return nGramModel.getSentenceLogProb(candidate, order);
	}

	@Override
	public double getChannelModelLogScore(Sentence sentence, int location, String candidate, HashSet<String> cands) {
		double total = getTotalCharcterCount(cands, dictionary);
		double count = dictionary.getCount(candidate);
		
		return count / total;
	}
}
