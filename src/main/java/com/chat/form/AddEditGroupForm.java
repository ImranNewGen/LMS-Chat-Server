package com.chat.form;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter@NoArgsConstructor
@AllArgsConstructor@ToString
public class AddEditGroupForm {
	private String newGname;
	private String oldGname;
	private List<String> details = new ArrayList<>();
}
