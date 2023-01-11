package com.jakewharton.dumbo

import com.jakewharton.dumbo.Tweet.UrlEntity
import java.time.Instant

data class Toot(
	val text: String,
	val posted: Instant,
	val language: String,
	val inReplyToId: String? = null,
) {
	companion object {
		fun fromTweet(tweet: Tweet, tootMap: Map<String, String?>): Toot {
			val text = buildString {
				var index = 0
				for (entity in tweet.entities) {
					when (entity) {
						is UrlEntity -> {
							if (entity.indices.first > index) {
								append(tweet.text.substring(index, entity.indices.first))
							}
							append(entity.url)
							index = entity.indices.last
						}
					}
				}
				if (index < tweet.text.length) {
					append(tweet.text.substring(index))
				}
			}
			val inReplyToId = if (tweet.inReplyToId == null) {
				null
			} else {
				checkNotNull(tootMap[tweet.inReplyToId]) {
					"Unable to map tweet ${tweet.id} replying to ${tweet.inReplyToId} without tootMap entry"
				}
			}
			return Toot(
				text = text,
				posted = tweet.createdAt,
				language = tweet.language,
				inReplyToId = inReplyToId,
			)
		}
	}
}
