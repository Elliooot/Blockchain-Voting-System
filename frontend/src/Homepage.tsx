import './Homepage.css';
import Header from './Header';
import ProjectCard from './ProjectCard'
import { CustomDrawer } from './Drawer';

import ShinyText from './blocks/TextAnimations/ShinyText/ShinyText';
import { Button } from 'flowbite-react';
// import { Drawer } from 'flowbite-react';
import { useState } from 'react';

function Homepage() {
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);
    return (
        <div className="homepage">
            <Header />
            <Button onClick={() => setIsDrawerOpen(true)}>Show Drawer From Homepage</Button>
            <CustomDrawer isOpen={isDrawerOpen} onClose={() => setIsDrawerOpen(false)} />
            <div className='content'>
            <h2 className='text-2xl font-bold'>Understanding the Challenges in Electronic Voting</h2>
            <ShinyText text="Electronic voting faces challenges related to security vulnerabilities and maintaining trustworthiness. Concerns about hacking, tampering, and unauthorized access undermine confidence in the electoral process. Blockchain technology can address these challenges by providing enhanced security measures, transparent record-keeping, and traceability." disabled={false} speed={3} className='custom-class' />
        </div>
        <main>
          <div className='project-list'>
            <ProjectCard
              imageUrl="https://via.placeholder.com/150"
              title="Project Title"
              description="This is a description of the project."
              link="https://example.com"
            />
            <ProjectCard
              imageUrl="https://via.placeholder.com/150"
              title="Another Project"
              description="This is another project description."
              link="https://example.com"
            />
            <ProjectCard
              imageUrl="https://via.placeholder.com/150"
              title="Third Project"
              description="This is a third project description."
              link="https://example.com"
            />
            <ProjectCard
              imageUrl="https://via.placeholder.com/150"
              title="Fourth Project"
              description="This is a fourth project description."
              link="https://example.com"
            />
          </div>
        </main>
        </div>
    );
}

export default Homepage;