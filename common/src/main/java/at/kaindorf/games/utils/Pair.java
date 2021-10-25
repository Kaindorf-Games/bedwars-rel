package at.kaindorf.games.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Pair<T1, T2>{
  private T1 first;
  private T2 second;
}
