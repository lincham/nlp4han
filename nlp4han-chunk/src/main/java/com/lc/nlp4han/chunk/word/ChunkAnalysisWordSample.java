package com.lc.nlp4han.chunk.word;

import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.Chunk;

/**
 * 基于词的组块分析样本类
 */
public class ChunkAnalysisWordSample extends AbstractChunkAnalysisSample
{

	/**
	 * 构造方法
	 * 
	 * @param words
	 *            词语数组
	 * @param tags
	 *            词语组块标记数组
	 */
	public ChunkAnalysisWordSample(String[] words, String[] tags)
	{
		super(words, tags, null);
	}

	/**
	 * 构造方法
	 * 
	 * @param words
	 *            词语序列
	 * @param tags
	 *            词语组块标记序列
	 */
	public ChunkAnalysisWordSample(List<String> words, List<String> tags)
	{
		super(words, tags, null);
	}

	@Override
	public Chunk[] toChunk()
	{
		if (scheme.equals("BIEOS"))
			return toChunkFromBIEOS();
		else if (scheme.equals("BIEO"))
			return toChunkFromBIEO();
		else
			return toChunkFromBIO();
	}

	private Chunk[] toChunkFromBIEOS()
	{
		List<Chunk> chunks = new ArrayList<>();
		int start = 0;
		boolean isChunk = false;
		String type = null;

		for (int i = 0; i < tags.size(); i++)
		{
			if (tags.get(i).equals("O") || tags.get(i).split("_")[1].equals("B"))
			{
				if (isChunk)
					chunks.add(new Chunk(type, join(tokens, start, i), start, i - 1));

				isChunk = false;
				if (!tags.get(i).equals("O"))
				{
					start = i;
					isChunk = true;
					type = tags.get(i).split("_")[0];
				}
			}
			else if (tags.get(i).split("_")[1].equals("S"))
			{
				if (isChunk)
					chunks.add(new Chunk(type, join(tokens, start, i), start, i - 1));

				type = tags.get(i).split("_")[0];
				chunks.add(new Chunk(type, tokens.get(i), i, i - 1));
				isChunk = false;
			}
		}

		if (isChunk)
			chunks.add(new Chunk(type, join(tokens, start, tags.size()), start, tags.size() - 1));

		return chunks.toArray(new Chunk[chunks.size()]);
	}

	private Chunk[] toChunkFromBIEO()
	{
		List<Chunk> chunks = new ArrayList<>();
		int start = 0;
		boolean isChunk = false;
		String type = null;

		for (int i = 0; i < tags.size(); i++)
		{
			if (tags.get(i).equals("O") || tags.get(i).split("_")[1].equals("B"))
			{
				if (isChunk)
					chunks.add(new Chunk(type, join(tokens, start, i), start, i - 1));

				isChunk = false;
				if (!tags.get(i).equals("O"))
				{
					start = i;
					isChunk = true;
					type = tags.get(i).split("_")[0];
				}
			}
		}

		if (isChunk)
			chunks.add(new Chunk(type, join(tokens, start, tags.size()), start, tags.size() - 1));

		return chunks.toArray(new Chunk[chunks.size()]);
	}

	private Chunk[] toChunkFromBIO()
	{
		List<Chunk> chunks = new ArrayList<>();
		int start = 0;
		boolean isChunk = false;
		String type = null;

		for (int i = 0; i < tags.size(); i++)
		{
			if (tags.get(i).equals("O") || tags.get(i).split("_")[1].equals("B"))
			{
				if (isChunk)
					chunks.add(new Chunk(type, join(tokens, start, i), start, i - 1));

				isChunk = false;
				if (!tags.get(i).equals("O"))
				{
					start = i;
					isChunk = true;
					type = tags.get(i).split("_")[0];
				}
			}
		}

		if (isChunk)
			chunks.add(new Chunk(type, join(tokens, start, tags.size()), start, tags.size() - 1));

		return chunks.toArray(new Chunk[chunks.size()]);
	}

	/**
	 * 拼接字符串word/pos word/pos ...
	 * 
	 * @param words
	 *            带拼接的词组
	 * @param poses
	 *            词组对应的词性
	 * @param start
	 *            拼接的开始位置
	 * @param end
	 *            拼接的结束位置
	 * @return 拼接后的字符串
	 */
	private List<String> join(List<String> words, int start, int end)
	{
		List<String> string = new ArrayList<>();
		for (int i = start; i < end; i++)
			string.add(words.get(i));

		return string;
	}

	@Override
	public String toString()
	{
		String string = "";

		Chunk[] chunks = toChunk();
		int j = 0;
		for (int i = 0; i < tokens.size(); i++)
		{
			if (j < chunks.length && i >= chunks[j].getStart() && i <= chunks[j].getEnd())
			{
				if (i == chunks[j].getEnd())
					string += chunks[j++] + "  ";
			}
			else
				string += tokens.get(i) + "  ";
		}

		return string.trim();
	}
}
