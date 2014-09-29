import java.io.IOException;

import org.jruby.Ruby;
import org.jruby.runtime.load.BasicLibraryService;

public class MmapService implements BasicLibraryService {
  public boolean basicLoad(final Ruby runtime) throws IOException {
    new com.jrubymmap.MmapLibrary().load(runtime, false);
    return true;
  }
}
