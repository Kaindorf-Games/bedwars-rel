package at.kaindorf.games.shop.Specials;

import at.kaindorf.games.utils.Utils;
import at.kaindorf.games.BedwarsRel;
import org.bukkit.Material;

public class MagnetShoe extends SpecialItem {

  @Override
  public Material getActivatedMaterial() {
    return null;
  }

  @SuppressWarnings("deprecation")
  @Override
  public Material getItemMaterial() {
    String item = BedwarsRel.getInstance()
        .getStringConfig("specials.magnetshoe.boots", "IRON_BOOTS");
    Material material = null;
    if (Utils.isNumber(item)) {
      material = Material.getMaterial(Integer.valueOf(item));
    } else {
      material = Material.getMaterial(item);
    }

    if (material == null) {
      return Material.IRON_BOOTS;
    }

    return material;
  }

}
