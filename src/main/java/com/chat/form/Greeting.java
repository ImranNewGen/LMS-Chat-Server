package com.chat.form;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter@NoArgsConstructor
@AllArgsConstructor@ToString
public class Greeting {
	private String content;
	private List<String> receiver = new ArrayList<>();
}
